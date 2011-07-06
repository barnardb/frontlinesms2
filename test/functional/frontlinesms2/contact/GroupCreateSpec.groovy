package frontlinesms2.contact

import frontlinesms2.*

class GroupCreateSpec extends grails.plugin.geb.GebSpec {
	def cleanup() {
		Group.findAll().each() {
			it.refresh()
			it.delete(failOnError:true, flush:true)
		}
	}

	def 'button to save new group is displayed and works'() {
		when:
			to CreateGroupPage
			def initNumGroups = Group.count()
			$("#group-details").name = 'People'
			btnSave.click()
		then:
			at ContactListPage
			assert Group.count() == (initNumGroups + 1)
	}

	def 'link to cancel creating a new group is displayed and goes back to main contact page'() {
		when:
			go 'contact/createGroup'
			def cancelGroup = $('#buttons').find('a').first()
			def btn = $("#buttons .list")
		then:
			assert cancelGroup.text() == "Cancel"
			assert cancelGroup.getAttribute('href') == "http://localhost:8080/frontlinesms2/contact/list"
	}
	
	def 'Errors are displayed when group fails to save'() {
		when:
			to CreateGroupPage
			btnSave.click()
		then:
			errorMessages.present
	}
}

class CreateGroupPage extends geb.Page {
	static url = 'contact/createGroup'
	static at = {
		title.endsWith('Create Group')
	}

	static content = {
		btnSave { $("#group-details .save") }
		errorMessages { $('.flash.message') }
	}
}
