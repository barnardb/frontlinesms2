package frontlinesms2

import grails.util.GrailsConfig

class ContactController {
	static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

	def beforeInterceptor = {
		params.max = params.max?: GrailsConfig.config.grails.views.pagination.max
	}

	def index = {
		redirect action: "list", params:params
	}

	def list = {
		def model = buildList()
		params.contactId = params.contactId?:model.contactInstanceList[0]?.id
		if(params.contactId) {
			redirect(action:'show', params:params)
		} else {
			model
		}
	}

	def buildList = {
		def groupInstance = params.groupId? Group.findById(params.groupId): null
		params.max = Math.min(params.max ? params.int('max') : 10, 100)
		params.sort = "name"

		def contactInstanceList, contactInstanceTotal
		if(groupInstance) {
			contactInstanceList = groupInstance.members as List
			contactInstanceTotal = groupInstance.members.size()
		} else {
			contactInstanceList = Contact.list(params)
			contactInstanceTotal = Contact.count()
		}
		if(params.flashMessage) {
			flash.message = params.flashMessage
		}
		[contactInstanceList: contactInstanceList,
				contactInstanceTotal: contactInstanceTotal,
				fieldInstanceList: CustomField.findAll(),
				groupInstanceList: Group.findAll(),
				groupInstanceTotal: Group.count(),
				contactsSection: groupInstance]
	}

	def show = {
		params.sort = "name"
		withContact { contactInstance ->
			def contactGroupInstanceList = contactInstance.groups?: []
			def contactFieldInstanceList = contactInstance.customFields
			[contactInstance:contactInstance,
					contactFieldInstanceList: contactFieldInstanceList,
					contactGroupInstanceList: contactGroupInstanceList,
					contactGroupInstanceTotal: contactGroupInstanceList.size(),
					nonContactGroupInstanceList: Group.findAllWithoutMember(contactInstance),
					uniqueFieldInstanceList: CustomField.getAllUniquelyNamed()] << buildList()
		}
	}

	def update = {
		withContact { contactInstance ->
			if (params.version) { // TODO create withVersionCheck closure for use in all Controllers
				def version = params.version.toLong()
				if (contactInstance.version > version) {
					contactInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'contact.label', default: 'Contact')] as Object[], "Another user has updated this Contact while you were editing")
					render(view: "show", model: [contactInstance: contactInstance])
					return
				}
			}
			contactInstance.properties = params
			updateData(contactInstance)
			render(view:'show', model:show()<<[contactInstance:contactInstance])
		}
	}
	
	def updateMultipleContacts = {
		if(params.contactIds) {
			def contactIds = params.contactIds.tokenize(',').unique()
			contactIds.each { id ->
				withContact id, { contactInstance ->
					updateData(contactInstance)
				}
			}
			flash.message = "${message(code: 'default.updated.message', args: [message(code: 'contact.label', default: 'Contact'), ''])}"
			redirect(action: 'show', params: [groupId: params.groupId])
		}
	}
	
	def createContact = {
		def model = [contactInstance:new Contact(params),
					contactFieldInstanceList: [],
					contactGroupInstanceList: [],
					contactGroupInstanceTotal: 0,
					nonContactGroupInstanceList: Group.findAll(),
					uniqueFieldInstanceList: CustomField.getAllUniquelyNamed()] << buildList()

		render(view:'show', model:model)
	}

	def createGroup = {
		def groupInstance = new Group()
		groupInstance.properties = params
		[groupInstance: groupInstance] << buildList()
	}

	def saveContact = {
		def contactInstance = new Contact(params)
		contactInstance.properties = params
		updateData(contactInstance)
		flash.message = "${message(code: 'default.updated.message', args: [message(code: 'contact.label', default: 'Contact'), contactInstance.id])}"
		redirect(action:'createContact')
	}
	
	def deleteContact = {
		def contactIds = params.contactIds ? params.contactIds.tokenize(',').unique() : [params.contactId]
		contactIds.each { id ->
			withContact id, { contactInstance ->
				Contact.get(contactInstance.id).delete()
			}
		}
		flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'contact.label', default: 'Contact'), ''])}"
		redirect(action: "list")		
	}

	def saveGroup = {
		def groupInstance = new Group(params)
		if (!groupInstance.hasErrors() && groupInstance.save(flush: true)) {
			flash.message = "${message(code: 'default.updated.message', args: [message(code: 'contact.label', default: 'Group'), groupInstance.id])}"
			redirect(controller:'group', action:'show', id: groupInstance.id, params: [flashMessage: flash.message])
		} else {
			flash.message = "error"
			redirect(action:'list', params: [flashMessage: flash.message, contactId: params.contactId])
		}
	}

	def newCustomField = {
		def contactInstance = params.contactId
		def customFieldInstance = new CustomField()
		customFieldInstance.properties = params
		[customFieldInstance: customFieldInstance,
				contactInstance: contactInstance]
	}

	private def withContact(contactId = params.contactId, Closure c) {
		def contactInstance = Contact.get(contactId)
		if (contactInstance) {
			c contactInstance
		} else {
			flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'contact.label', default: 'Contact'), params.id])}"
			redirect(action: "list")
		}
	}
	
	def multipleContactGroupList = {
		if(!params.contactIds) {
			return []
		}
		def contactIds = params.contactIds ? params.contactIds.tokenize(',').unique() : []
		def sharedGroupInstanceList = []
		def groupInstanceList = []
		contactIds.each { id ->
			withContact id, { contactInstance ->
				groupInstanceList << contactInstance.getGroups()
			}
		}
		sharedGroupInstanceList = getSharedGroupList(groupInstanceList)
		def nonSharedGroupInstanceList = getNonSharedGroupList(Group.findAll(), sharedGroupInstanceList)
		render(view: "_multiple_contact_view", model: [sharedGroupInstanceList: sharedGroupInstanceList,
			nonSharedGroupInstanceList: nonSharedGroupInstanceList])
	}
	
	private def getSharedGroupList(Collection groupList) {
		def groupIDList = groupList.collect {it.id}
		def intersect = groupIDList.get(0)
		for(int i=1 ; i<groupIDList.size; i++) {
			if(!intersect.disjoint(groupIDList.get(i))) {
				intersect = intersect.intersect(groupIDList.get(i))
			} else {
				intersect = []
				break
			}
		}
		if(intersect) {
			return intersect.collect { Group.findById(it) }
		}
	}
	
	private def getNonSharedGroupList(Collection groupList1, Collection groupList2) {
		def groupIdList1 = groupList1.collect {it.id}
		def groupIdList2 = groupList2.collect {it.id}
		def nonSharedGroupList = (groupIdList1 - groupIdList2).collect { Group.findById(it) } ?: []
		nonSharedGroupList
	}

	private def updateData(Contact contactInstance) {
		// Check for errors in groupsToAdd and groupsToRemove
		def groupsToAdd = params.groupsToAdd.tokenize(',').unique()
		def groupsToRemove = params.groupsToRemove.tokenize(',')
		
		def fieldsToAdd = params.fieldsToAdd ? params.fieldsToAdd.tokenize(',') : []
		def fieldsToRemove = params.fieldsToRemove ? params.fieldsToRemove.tokenize(',') : []
		if(!groupsToAdd.disjoint(groupsToRemove)) {
			contactInstance.errors.reject('Cannot add and remove from the same group!')
		} else if (contactInstance.validate() && !contactInstance.hasErrors()) {
			groupsToAdd.each() { id ->
				contactInstance.addToGroups(Group.get(id))
			}
			groupsToRemove.each() { id ->
				contactInstance.removeFromGroups(Group.get(id))
			}

			fieldsToAdd.each() { name ->
				def existingFields = CustomField.findAllByNameAndContact(name, contactInstance)
				def fieldsByName = params."$name"
				if(fieldsByName?.class != String) {
					fieldsByName.each() { val ->
						if(val != "" && !existingFields.value.contains(val))
							contactInstance.addToCustomFields(new CustomField(name: name, value: val)).save(flush:true)
							existingFields = CustomField.findAllByNameAndContact(name, contactInstance)
					}
				} else if(fieldsByName != "" && !existingFields.value.contains(fieldsByName)) {
					contactInstance.addToCustomFields(new CustomField(name: name, value: fieldsByName))
				}
			}
			fieldsToRemove.each() { id ->
				def toRemove = CustomField.get(id)
				if(toRemove)
					toRemove.delete(failOnError: true, flush:true)
			}
			
			if(contactInstance.save(flush:true)) {
				flash.message = "${message(code: 'default.updated.message', args: [message(code: 'contact.label', default: 'Contact'), contactInstance.id])}"
				def redirectParams = [contactId:contactInstance.id]
				if(params.groupId) redirectParams << [groupId: params.groupId]
			}
		}
	}
}
