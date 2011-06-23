package frontlinesms2.contact

import frontlinesms2.*

class GroupViewSpec extends GroupGebSpec {
	def cleanup() {
		Group.findAll().each() {
			it.refresh()
			it.delete(failOnError:true, flush:true)	
		}
	}

	def 'Group menu is displayed'() {
		given:
			createTestGroups()
			def groupNames = ['Listeners', 'Friends']
		when:
			to ContactListPage
		then:
			groupsList.children().collect() {
				it.text()
			} == groupNames
	}

	def 'Group menu item is highlighted when viewing corresponding group'() {
		given:
			createTestGroups()
		when:
			to FriendsGroupPage
		then:
			selectedMenuItem.text() == 'Friends'
		when:
			Contact c = new Contact(name:'Mildred').save(failOnError:true, flush:true)
		    c.addToGroups(Group.findByName('Friends'))
		    c.save(flush: true)
			to FriendsGroupPage
		then:
			selectedMenuItem.text() == 'Friends'
	}

	def 'Group members list is displayed when viewing corresponding group'() {
		given:
			createTestGroupsAndContacts()
			def friendsContactNames = ['Bobby', 'Duchamps']
		when:
			to FriendsGroupPage
		then:
			contactsList.children().collect() { it.text() }.sort() == friendsContactNames
	}

	def 'Group members list has correct href when viewing corresponding group'() {
		given:
			createTestGroupsAndContacts()
		when:
			to FriendsGroupPage
			def links = contactsList.find('a')
		then:
			links.size() == 2
			links.each() {
				assert it.@href ==~ '/frontlinesms2/group/show/\\d+/contact/show/\\d+'
			}
	}
}

class FriendsGroupPage extends geb.Page {
	static getUrl() { "group/show/${Group.findByName('Friends').id}" }
	static content = {
		selectedMenuItem { $('#contacts-menu .selected') }
		contactsList { $('#contacts') }
	}
}
