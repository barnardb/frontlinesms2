package frontlinesms2.search

import frontlinesms2.*

class SearchSpec extends grails.plugin.geb.GebSpec {
	def setup() {
		createTestGroups()
		createTestPollsAndFolders()
		createTestMessages()
	}
	
	def "clicking on the search button links to the result show page"() {
		when:
			to SearchPage
			searchBtn.present()
			searchBtn.click()
		then:
			at SearchPage
	}
	
	def "group list and activity lists are displayed when they exist"() {
		when:
			to SearchPage
		then:
			searchFrm.find('select', name:'groupId').children().collect() { it.text() } == ['Select group','Listeners', 'Friends']
			searchFrm.find('select', name:'activityId').children().collect() { it.text() } == ['Select activity / folder', "Miauow Mix", 'Work']
	}
	
	def "search description is shown in header when searching by group"() {
		when:
			to SearchPage
			searchFrm.searchString = "test"
			searchBtn.click()
		then:
			searchDescription.text() == 'Searching in all messages'
	}
	
	def "search string is still shown on form submit and consequent page reload"() {
		given:
			to SearchPage
			searchFrm.searchString = 'bacon'
		when:
			searchBtn.click()
		then:
			searchFrm.searchString == 'bacon'
	}
	
	def "selected activity is still selected on form submit and consequent page reload"() {
		given:
			to SearchPage
			def a = Folder.findByName("Work")
			searchFrm.activityId = "folder-${a.id}"
		when:
			searchBtn.click()
		then:
			searchFrm.activityId == ["folder-${a.id}"]
	}
	
	def "'Export Results' link is disabled when search is null "() {
		when:
			to SearchPage
		then:
			!$('h2:nth-child(2) div#export-results a').present();
	}
	
//	def "message list returned from a search operation is displayed, regardless of search case"() {
//		when:
//			to SearchPage
//			searchFrm.searchString = "AlEx"
//			searchBtn.click()
//			def rowContents = $('#messages tbody tr:nth-child(1) td')*.text()
//		then:
//			rowContents[1] == 'Alex'
//			rowContents[2] == 'hi alex'
//			rowContents[3] ==~ /[0-9]{2}-[A-Z][a-z]{2}-[0-9]{4} [0-9]{2}:[0-9]{2}/
//			sleep(1000)
//	}
	
	private createTestGroups() {
		new Group(name: 'Listeners').save(flush: true)
		new Group(name: 'Friends').save(flush: true)
	}
	
	private createTestMessages() {
		[new Fmessage(src:'Doe', dst:'+254987654', text:'meeting at 11.00'),
				new Fmessage(src:'Alex', dst:'+254987654', text:'hi alex')].each() {
			it.status = MessageStatus.INBOUND
			it.save(failOnError:true)
		}
	}
	
	private createTestPollsAndFolders() {
		def chickenResponse = new PollResponse(value:'chicken')
		def liverResponse = new PollResponse(value:'liver')
		Poll p = new Poll(title:'Miauow Mix', responses:[chickenResponse, liverResponse]).save(failOnError:true, flush:true)
		Folder f = new Folder(name: "Work").save(failOnError:true, flush:true)
	}
}

class SearchPage extends geb.Page {
	static url = 'search'
	static at = {
		title.startsWith('Results')
	}
	static content = {
		searchFrm { $('#search-details') }
		searchBtn { $('#search-details .buttons .search') }
		searchDescription { $('#search-description') }
	}
}
