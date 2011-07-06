package frontlinesms2.contact

import frontlinesms2.*
import java.util.regex.*

class AddCustomFieldSpec extends grails.plugin.geb.GebSpec {
	def "selecting add custom field from dropdown opens the popup"() {
		when:
			Contact bob = new Contact(name:'Bob').save(failOnError: true, flush: true)
			go "contact/show/${bob.id}"
			$("#new-field-dropdown option:nth-child(1)").click()
			waitFor {$('div#custom-field-popup').displayed()}
		then:
			$('div#custom-field-popup').displayed()
		cleanup:
			bob.delete(failOnError: true, flush: true)
	}

	def "should add the manually entered custom fields to the list "() {
		when:
			Contact bob = new Contact(name:'Bob').save(failOnError: true, flush: true)
			go "contact/show/${bob.id}"
			$("#new-field-dropdown option:nth-child(1)").click()
			waitFor {$('div#custom-field-popup').displayed()}
			$("#custom-field-name").value("planet")
			$("#custom-field-value").value("mars")
			def btnCreate = $('.buttons .create')
			btnCreate.click()
		then:
			$('#custom-field-list li').find('label').text() == "planet"
			$('#custom-field-list li').find('input').value() == "mars"
		cleanup:
			bob.delete(failOnError: true, flush: true)
	}
}

class CustomFieldPage extends geb.Page {
	static url = 'contact/show'
	static content = {
		selectedMenuItem { $('#messages-menu .selected') }
		messagesList { $('#messages-submenu') }
	}
}