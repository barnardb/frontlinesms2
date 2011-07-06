package frontlinesms2.contact

import frontlinesms2.*

import geb.Browser
import grails.plugin.geb.GebSpec

class ContactShowSpec extends ContactGebSpec {
	def setup() {
		createTestContacts()
	}

	def cleanup() {
		deleteTestContacts()
	}

	def 'contacts link to their details'() {
		given:
			def alice = Contact.findByName('Alice')
		when:
			go 'contact'
		then:
			def firstContactListItem = $('#contacts').children().first()
			def anchor = firstContactListItem.children('a').first()
			anchor.text() == 'Alice'
			anchor.getAttribute('href') == "http://localhost:8080/frontlinesms2/contact/show/${alice.id}"
	}

	def 'selected contacts show message statistics' () {
		given:
	  		def alice = Contact.findByName('Alice')
		when:
	  		go "contact/show/${alice.id}"
		then:
	        $("#message-count p").first().text() == '0 messages sent'
	        $("#message-count p").last().text() == '0 messages received'
	}

	def 'contact with no name can be clicked and edited because his address is displayed'() {
		when:
			def empty = new Contact(name:'', address:"+987654321")
			empty.save(failOnError:true)
			go "contact/list"
			def noName = Contact.findByAddress('+987654321')
		then:
			noName != null
			$('a', href:"http://localhost:8080/frontlinesms2/contact/show/${noName.id}").text().trim() == noName.address
	}

	def 'selected contact is highlighted'() {
		given:
			def alice = Contact.findByName('Alice')
			def bob = Contact.findByName('Bob')
		when:
			go "contact/show/${alice.id}"
		then:
			assertContactSelected('Alice')
		    
		when:
			go "contact/show/${bob.id}"
		then:
			assertContactSelected('Bob')
	}

	def 'selected contact details are displayed'() {
		given:
			def alice = Contact.findByName('Alice')
		when:
			go "contact/show/${alice.id}"
		then:
			assertFieldDetailsCorrect('name', 'Name', 'Alice')
			assertFieldDetailsCorrect('address', 'Address', '+2541234567')
			assertFieldDetailsCorrect('notes', 'Notes', 'notes')
	}

	def 'contact with no groups has NO GROUPS message visible'() {
		given:
			def alice = Contact.findByName('Alice')
		when:
			go "contact/show/${alice.id}"
		then:
			$('#no-groups').@style == ''
	}

	def 'contact with groups has NO GROUPS message hidden'() {
		given:
			createTestGroups()
			def bob = Contact.findByName('Bob')
		when:
			go "contact/show/${bob.id}"
		then:
			!$('#no-groups').displayed
		cleanup:
			deleteTestGroups()
	}

	def assertContactSelected(String name) {
		def selectedChildren = $('#contacts').children('li.selected')
		assert selectedChildren.size() == 1
		assert selectedChildren.text() == name
		true
	}
}

class EmptyContactPage extends geb.Page {
	static def getUrl() {
		"contact/show/${Contact.findByName('').id}"
	}

	static at = {
		assert url == "contact/show/${Contact.findByName('').id}"
		true
	}

	static content = {
		frmDetails { $("#contact-details") }
		btnSave { frmDetails.find('.update') }
	}
}
