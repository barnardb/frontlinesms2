package frontlinesms2.contact

import frontlinesms2.*

import geb.Browser
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.Keys

class SubscriptionSpec extends GroupGebSpec  {

	def setup() {
		createTestGroups()
	}

	def "should move to the next tab if all the values are provided"() {
		when:
			goToManageSubscriptions()
			inputKeywords("subscriptionKey", "ADD")
			inputKeywords("unsubscriptionKey", "REMOVE")
			selectAValueFromDropDown()
			$('#nextPage').click()
			waitFor { $("#tabs-3").displayed }
		then:
			$("#tabs-3").displayed
	}

	def "should be able to set subscription keywords for a group"() {
		when:
			goToManageSubscriptions()
			inputKeywords("subscriptionKey", "ADD")
			inputKeywords("unsubscriptionKey", "REMOVE")
			selectAValueFromDropDown()
			$('#nextPage').click()
			waitFor { $("#tabs-3").displayed }
		then:
			$("#done").click()
			waitFor({title == 'Inbox'})
			$('div.flash').text().contains('Group updated successfully')
			def groupUpdated = Group.findByName("Listeners").refresh()
			groupUpdated.subscriptionKey == "ADD"
			groupUpdated.unsubscriptionKey == "REMOVE"
	}


	def "should be able to set subscription keyword alone for a group"() {
		when:
			goToManageSubscriptions()
			inputKeywords("subscriptionKey", "ADD")
			selectAValueFromDropDown()
			$('#nextPage').click()
			waitFor { $("#tabs-3").displayed }
		then:
			$("#done").click()
			waitFor({title == 'Inbox'})
			$('div.flash').text().contains('Group updated successfully')
			def groupUpdated = Group.findByName("Listeners").refresh()
			groupUpdated.subscriptionKey == "ADD"
			!groupUpdated.unsubscriptionKey
	}


	def "should be able to set unsubscription keyword alone for a group"() {
		when:
			goToManageSubscriptions()
			inputKeywords("unsubscriptionKey", "REMOVE")
			selectAValueFromDropDown()
			$('#nextPage').click()
			waitFor { $("#tabs-3").displayed }
		then:
			$("#done").click()
			waitFor({title == 'Inbox'})
			$('div.flash').text().contains('Group updated successfully')
			def groupUpdated = Group.findByName("Listeners").refresh()
			!groupUpdated.subscriptionKey
			groupUpdated.unsubscriptionKey == "REMOVE"
	}


	def "should not go to the next tab if the subscription checkbox is selected and no value given"() {
		when:
			goToManageSubscriptions()
			inputKeywords("subscriptionKey", "")
			selectAValueFromDropDown()
			$('#nextPage').click()
			waitFor { !($('div.error-panel').text().isEmpty())}
		then:
			$('.error-panel').text().contains("please enter all the details")
	}


	def "should not go to the next tab if the unsubscription checkbox is selected and no value given"() {
		when:
			goToManageSubscriptions()
			inputKeywords("unsubscriptionKey", "")
			selectAValueFromDropDown()
			$('#nextPage').click()
			waitFor { !($('div.error-panel').text().isEmpty())}
		then:
			$('.error-panel').text().contains("please enter all the details")
	}


	def "should check the checkbox on click of subscription key text box"() {
		when:
			goToManageSubscriptions()
			assert !$("input", value: "subscriptionKey").getAttribute("checked")
			inputKeywords("subscriptionKey","")
		then:
			$("input", value: "subscriptionKey").getAttribute("checked")

	}

	def "should check the checkbox on click of unsubscription key text box"() {
		when:
			goToManageSubscriptions()
			assert !$("input", value: "unsubscriptionKey").getAttribute("checked")
			inputKeywords("unsubscriptionKey","")
		then:
			$("input", value: "unsubscriptionKey").getAttribute("checked")
	}

	def "should not go to next tab if no group is selected"() {
		when:
			goToManageSubscriptions()
			inputKeywords("subscriptionKey", "ADD")
			inputKeywords("unsubscriptionKey", "REMOVE")
			$('#nextPage').click()
			waitFor { !($('div.error-panel').text().isEmpty())}

		then:
			$('.error-panel').text().contains("please enter all the details")
	}

	def "should not go to the next tab when all the fields are empty"() {
		when:
			goToManageSubscriptions()
			$('#nextPage').click()
			waitFor { !($('div.error-panel').text().isEmpty())}

		then:
			$('.error-panel').text().contains("please enter all the details")
	}

	private def goToManageSubscriptions() {
		go "/frontlinesms2/message"
		$("#create-activity a").click()
		waitFor {$('#tabs-1').displayed}
		$("input", name: "activity").value("subscription")
		$("#done").click()
		waitFor {$("#ui-dialog-title-modalBox").text() == "Manage Subscription"}
	}

	private def inputKeywords(keyName, keyValue) {
		def element = $("input", name:keyName)
		element.jquery.trigger('focus')
		element.value(keyValue)
	}
	private def selectAValueFromDropDown() {
		$("select", id:"id" ).value(Group.list()[0].id.toString());
	}
}

