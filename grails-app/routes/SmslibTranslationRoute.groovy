class SmslibTranslationRoute {
	def configure = {	
		from('seda:raw-smslib')
				.beanRef('smslibTranslationService', 'toFmessage')
				.to('seda:fmessages-to-store')
				.id('smslib-translation')
	}
}
