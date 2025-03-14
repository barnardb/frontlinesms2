package frontlinesms2.poll

import frontlinesms2.*

class CreateNewPollSpec extends PollGebSpec {
	def 'existing polls appear in activities section of messages'() {
		given:
			createTestPolls()
		when:
			go 'message'
		then:
			$('#activities-submenu li')[0].text().contains('Football Teams') // TODO: find/implement array contains
			$('#activities-submenu li')[1].text().contains('Shampoo Brands')
			$('#activities-submenu li')[2].text().contains('Rugby Brands')
	}
}

class CreatePollPage extends geb.Page {
	static url = 'poll/create'
	static at = {
		// FIXME put in a test here
		true
	}
	static content = {
		frmDetails { $("#poll-details") }
		errorMessages(required:false) { $('.flash.errors') }
	}
}
