package frontlinesms2.search

import frontlinesms2.*

class SearchSpec extends grails.plugin.geb.GebSpec {
	def setup() {
		createTestGroups()
		createTestPollsAndFolders()
		createTestMessages()
		$("#groupId option:nth-child(1)").click()
		$("#activityId option:nth-child(1)").click()
	}
	
	def cleanup() {
		deleteTestGroups()
		deleteTestPollsAndFolders()
		deleteTestMessages()
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
	
	def "search description is shown in header when searching by group and poll"() {
		when:
			to SearchPage
			searchFrm.searchString = "test"
			$("#groupId option:nth-child(2)").click()
			$("#activityId option:nth-child(2)").click()
			searchBtn.click()
		then:
			searchDescription.text() == "Searching in 'Listeners' and 'Miauow Mix'"
	}
	
	def "message list returned from a search operation is displayed"() {
		when:
			to SearchPage
			searchFrm.searchString = "alex"
			searchBtn.click()
			def rowContents = $('#messages tbody tr:nth-child(1) td')*.text()
		then:
			rowContents[0] == 'Alex'
			rowContents[1] == 'hi alex'
			rowContents[2] ==~ /[0-9]{2}-[A-Z][a-z]{2}-[0-9]{4} [0-9]{2}:[0-9]{2}/
	}
	
	def "message list returned from a search operation is displayed, regardless of search case"() {
		when:
			to SearchPage
			searchFrm.searchString = "AlEx"
			searchBtn.click()
			def rowContents = $('#messages tbody tr:nth-child(1) td')*.text()
		then:
			rowContents[0] == 'Alex'
			rowContents[1] == 'hi alex'
			rowContents[2] ==~ /[0-9]{2}-[A-Z][a-z]{2}-[0-9]{4} [0-9]{2}:[0-9]{2}/
	}
	
	def "search string is still shown on form submit and consequent page reload"() {
		given:
			to SearchPage
			searchFrm.searchString = 'bacon'
		when:
			searchBtn.click()
		then:
			println $('#search-details')
			$('#searchString').getAttribute('value') == "bacon"
	}
	
	def "selected group is still selected on form submit and consequent page reload"() {
		given:
			to SearchPage
			def g = Group.findByName("Friends")
			$("#groupId option:nth-child(3)").click()
		when:
			searchBtn.click()
		then:
			$("#groupId option:nth-child(3)").getAttribute('selected') == "true"
	}
	
	def "selected activity is still selected on form submit and consequent page reload"() {
		given:
			to SearchPage
			def a = Folder.findByName("Work")
			$("#activityId option:nth-child(3)").click()
		when:
			searchBtn.click()
		then:
			$("#activityId option:nth-child(3)").getAttribute('selected') == "true"
	}
	
//	def 'message actions menu is displayed for all individual messages'() {
//		given:
//			createTestMessages()
//		when:
//			searchFrm.searchString = "Bob"
//			searchBtn.click()
//			def rowContents = $('#messages tbody tr:nth-child(1) td')*.text()
//			def actions = $('#message-actions li').children('a')*.text()
//		then:
//			actions[0] == 'Miauow Mix'
//		cleanup:
//			deleteTestMessages()
//	}
//	
	
	private createTestGroups() {
		new Group(name: 'Listeners').save(flush: true)
		new Group(name: 'Friends').save(flush: true)
	}
	
	private createTestMessages() {
		[new Fmessage(src:'Doe', dst:'+254987654', text:'meeting at 11.00'),
				new Fmessage(src:'Alex', dst:'+254987654', text:'hi alex')].each() {
			it.inbound = true
			it.save(failOnError:true)
		}
	}
	
	private createTestPollsAndFolders() {
		def chickenResponse = new PollResponse(value:'chicken')
		def liverResponse = new PollResponse(value:'liver')
		Poll p = new Poll(title:'Miauow Mix', responses:[chickenResponse, liverResponse]).save(failOnError:true, flush:true)
		Folder f = new Folder(name: "Work").save(failOnError:true, flush:true)
	}
	private deleteTestPollsAndFolders() {
		Poll.findAll()*.delete(flush:true, failOnError:true)
		Folder.findAll()*.delete(flush:true, failOnError:true)
	}
	
	private deleteTestMessages() {
		Fmessage.findAll()*.delete(flush:true, failOnError:true)
	}
	
	private deleteTestGroups() {
		Group.findAll()*.delete(flush:true, failOnError:true)
	}
}

class SearchPage extends geb.Page {
	static url = 'search'
	static at = {
		title.startsWith('Search')
	}
	static content = {
		searchFrm { $('#search-details') }
		searchBtn { $('.buttons .search') }
		searchDescription { $('#search-description') }
	}
}