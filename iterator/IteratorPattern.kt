package iterator

import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class Profile(val email: String, val name: String, vararg contacts: String) {
    private val contacts: MutableMap<String, MutableList<String>> = HashMap()
    fun getContacts(contactType: String): List<String> {
        if (!contacts.containsKey(contactType)) {
            contacts[contactType] = ArrayList()
        }
        return contacts[contactType]!!
    }

    init {
        for (contact in contacts) {
            val parts = contact.split(":".toRegex()).toTypedArray()
            var contactType = "friend"
            var contactEmail: String
            if (parts.size == 1) {
                contactEmail = parts[0]
            } else {
                contactType = parts[0]
                contactEmail = parts[1]
            }
            if (!this.contacts.containsKey(contactType)) {
                this.contacts[contactType] = ArrayList()
            }
            this.contacts[contactType]!!.add(contactEmail)
        }
    }
}

interface ProfileIterator {
    operator fun hasNext(): Boolean
    val next: Profile?

    fun reset()
}

interface SocialNetwork {
    fun createFriendsIterator(profileEmail: String): ProfileIterator
    fun createCoworkersIterator(profileEmail: String): ProfileIterator
}

class FacebookIterator(facebook: Facebook, type: String, email: String) :
    ProfileIterator {
    private val facebook: Facebook = facebook
    private val type: String = type
    private val email: String
    private var currentPosition = 0
    private val emails: MutableList<String> = ArrayList()
    private val profiles: MutableList<Profile?> = ArrayList<Profile?>()
    private fun lazyLoad() {
        if (emails.size == 0) {
            val profiles: List<String> ? = facebook.requestProfileFriendsFromFacebook(
                email, type
            )
            profiles?.let {
                for (profile in profiles) {
                    emails.add(profile)
                    this.profiles.add(null)
                }
            }
        }
    }

    override operator fun hasNext(): Boolean {
        lazyLoad()
        return currentPosition < emails.size
    }

    override val next: Profile?
        get() {
            if (!hasNext()) {
                return null
            }
            val friendEmail = emails[currentPosition]
            var friendProfile: Profile? = profiles[currentPosition]
            if (friendProfile == null) {
                friendProfile = facebook.requestProfileFromFacebook(friendEmail)
                profiles[currentPosition] = friendProfile
            }
            currentPosition++
            return friendProfile
        }

    override fun reset() {
        currentPosition = 0
    }

    init {
        this.email = email
    }
}

class Facebook(cache: List<Profile>?) :
    SocialNetwork {
    private var profiles: List<Profile>? = null
    fun requestProfileFromFacebook(profileEmail: String): Profile? {
        simulateNetworkLatency()
        println("Facebook: Loading profile '$profileEmail' over the network...")
        return findProfile(profileEmail)
    }

    fun requestProfileFriendsFromFacebook(
        profileEmail: String,
        contactType: String
    ): List<String>? {
        simulateNetworkLatency()
        println("Facebook: Loading '$contactType' list of '$profileEmail' over the network...")
        val profile = findProfile(profileEmail)
        return profile?.getContacts(contactType)
    }

    private fun findProfile(profileEmail: String): Profile? {
        for (profile in profiles!!) {
            if (profile.email == profileEmail) {
                return profile
            }
        }
        return null
    }

    private fun simulateNetworkLatency() {
        try {
            Thread.sleep(2500)
        } catch (ex: InterruptedException) {
            ex.printStackTrace()
        }
    }

    override fun createFriendsIterator(profileEmail: String): ProfileIterator {
        return FacebookIterator(this, "friends", profileEmail)
    }

    override fun createCoworkersIterator(profileEmail: String): ProfileIterator {
        return FacebookIterator(this, "coworkers", profileEmail)
    }

    init {
        if (cache != null) {
            profiles = cache
        } else {
            profiles = ArrayList()
        }
    }
}

class LinkedInIterator(
    private val linkedIn: LinkedIn,
    private val type: String,
    private val email: String
) :
    ProfileIterator {
    private var currentPosition = 0
    private val emails: MutableList<String> = ArrayList()
    private val contacts: MutableList<Profile?> = ArrayList()
    private fun lazyLoad() {
        if (emails.size == 0) {
            val profiles = linkedIn.requestRelatedContactsFromLinkedInAPI(email, type)
            for (profile in profiles!!) {
                emails.add(profile)
                contacts.add(null)
            }
        }
    }

    override fun hasNext(): Boolean {
        lazyLoad()
        return currentPosition < emails.size
    }

    override val next: Profile?
        get() {
            if (!hasNext()) {
                return null
            }
            val friendEmail = emails[currentPosition]
            var friendContact = contacts[currentPosition]
            if (friendContact == null) {
                friendContact = linkedIn.requestContactInfoFromLinkedInAPI(friendEmail)
                contacts[currentPosition] = friendContact
            }
            currentPosition++
            return friendContact
        }

    override fun reset() {
        currentPosition = 0
    }
}

class LinkedIn(cache: List<Profile>?) : SocialNetwork {
    private var contacts: List<Profile>? = null
    fun requestContactInfoFromLinkedInAPI(profileEmail: String): Profile? {
        simulateNetworkLatency()
        println("LinkedIn: Loading profile '$profileEmail' over the network...")
        return findContact(profileEmail)
    }

    fun requestRelatedContactsFromLinkedInAPI(
        profileEmail: String,
        contactType: String
    ): List<String>? {
        simulateNetworkLatency()
        println("LinkedIn: Loading '$contactType' list of '$profileEmail' over the network...")
        val profile = findContact(profileEmail)
        return profile?.getContacts(contactType)
    }

    private fun findContact(profileEmail: String): Profile? {
        for (profile in contacts!!) {
            if (profile.email == profileEmail) {
                return profile
            }
        }
        return null
    }

    private fun simulateNetworkLatency() {
        try {
            Thread.sleep(2500)
        } catch (ex: InterruptedException) {
            ex.printStackTrace()
        }
    }

    override fun createFriendsIterator(profileEmail: String): ProfileIterator {
        return LinkedInIterator(this, "friends", profileEmail)
    }

    override fun createCoworkersIterator(profileEmail: String): ProfileIterator {
        return LinkedInIterator(this, "coworkers", profileEmail)
    }

    init {
        if (cache != null) {
            contacts = cache
        } else {
            contacts = ArrayList()
        }
    }
}

class SocialSpammer(var network: SocialNetwork) {
    var iterator: ProfileIterator? = null
    fun sendSpamToFriends(profileEmail: String?, message: String) {
        println("\nIterating over friends...\n")
        iterator = network.createFriendsIterator(profileEmail!!)
        while (iterator!!.hasNext()) {
            val profile = iterator!!.next
            sendMessage(profile!!.email, message)
        }
    }

    fun sendSpamToCoworkers(profileEmail: String?, message: String) {
        println("\nIterating over coworkers...\n")
        iterator = network.createCoworkersIterator(profileEmail!!)
        while (iterator!!.hasNext()) {
            val profile = iterator!!.next
            sendMessage(profile!!.email, message)
        }
    }

    private fun sendMessage(email: String, message: String) {
        println("Sent message to: '$email'. Message body: '$message'")
    }
}

object B03Iterator {
    var scanner: Scanner = Scanner(System.`in`)
    @JvmStatic
    fun main(args: Array<String>) {
        println("Please specify social network to target spam tool (default:Facebook):")
        println("1. Facebook")
        println("2. LinkedIn")
        val choice: String = scanner.nextLine()
        val network: SocialNetwork
        network = if (choice == "2") {
            LinkedIn(createTestProfiles())
        } else {
            Facebook(createTestProfiles())
        }
        val spammer = SocialSpammer(network)
        spammer.sendSpamToFriends(
            "anna.smith@bing.com",
            "Hey! This is Anna's friend Josh. Can you do me a favor and like this post [link]?"
        )
        spammer.sendSpamToCoworkers(
            "anna.smith@bing.com",
            "Hey! This is Anna's boss Jason. Anna told me you would be interested in [link]."
        )
    }

    fun createTestProfiles(): List<Profile> {
        val data: MutableList<Profile> = ArrayList()
        data.add(
            Profile(
                "anna.smith@bing.com",
                "Anna Smith",
                "friends:mad_max@ya.com",
                "friends:catwoman@yahoo.com",
                "coworkers:sam@amazon.com"
            )
        )
        data.add(
            Profile(
                "mad_max@ya.com",
                "Maximilian",
                "friends:anna.smith@bing.com",
                "coworkers:sam@amazon.com"
            )
        )
        data.add(Profile("bill@microsoft.eu", "Billie", "coworkers:avanger@ukr.net"))
        data.add(Profile("avanger@ukr.net", "John Day", "coworkers:bill@microsoft.eu"))
        data.add(
            Profile(
                "sam@amazon.com",
                "Sam Kitting",
                "coworkers:anna.smith@bing.com",
                "coworkers:mad_max@ya.com",
                "friends:catwoman@yahoo.com"
            )
        )
        data.add(
            Profile(
                "catwoman@yahoo.com",
                "Liza",
                "friends:anna.smith@bing.com",
                "friends:sam@amazon.com"
            )
        )
        return data
    }
}
