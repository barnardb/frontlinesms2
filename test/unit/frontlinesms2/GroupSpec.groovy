package frontlinesms2

import spock.lang.*
import grails.plugin.spock.*

class GroupSpec extends UnitSpec {
	def "group may have a name"() {
		when:
			Group g = new Group()
			assert g.name == null
			g.name = 'People'
		then:
			g.name == 'People'
	}

	def "group must have a name"() {
		when:
			def noNameGroup = new Group()
			def namedGroup = new Group(name:'People')
			mockForConstraintsTests(Group, [noNameGroup, namedGroup])
		then:
			!noNameGroup.validate()
			namedGroup.validate()
	}

	def "group must have unique name"() {
		when:
			def name1Group = new Group(name:'Same')
			def name2Group = new Group(name:'Same')
			mockForConstraintsTests(Group, [name1Group, name2Group])
		then:
			!name1Group.validate()
			!name2Group.validate()
	}

	def "group name must be less than 255 characters"() {
		when:
			def longNameGroup = new Group(name:'0123456789abcdef'*16)
			mockForConstraintsTests(Group, [longNameGroup])
		then:
			assert longNameGroup.name.length() > 255
			!longNameGroup.validate()
	}

	def "should get all the member addresses for a group"() {
		setup:
			def group = new Group(name: "Sahara",
							members:[new Contact(address: "12345"), new Contact(address: "56484")])
		when:
			def result = group.getAddresses()
		then:
			result.containsAll(["12345", "56484"])

	}

	def "should list all the group names with a count of number of people in the group"() {
		setup:
			def sahara = new Group(name: "sahara", members: [new Contact(name: "Bob", address: "address1"),
															new Contact(name: "Jim", address: "address2")])
			def thar = new Group(name: "thar", members: [new Contact(name: "Kate", address: "address3")])
			mockDomain(Group, [sahara, thar])
		when:
			def result = Group.getGroupDetails()
		then:
			result['sahara'] == 2
			result['thar'] == 1
	}

}

