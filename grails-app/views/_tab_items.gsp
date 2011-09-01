<li>
    <g:link class="tab ${!(params['archived']?.toBoolean()) && ['message','folder','poll'].contains(params.controller)?'selected':''}" url="${[controller:'message']}"	id="tab-messages">Messages ${frontlinesms2.Fmessage.countUnreadMessages()}</g:link>
</li>

<li>
    <g:link class="tab ${params['archived'] == 'true'? 'selected':''}" controller='message' action="inbox" params="['archived': true]">Archive</g:link>
</li>

<li>
    <g:link class="tab ${params.controller=='contact'?'selected':''}" url="${[controller:'contact']}" id="tab-contacts">Contacts</g:link>
</li>
<li>
    <g:link  class="tab ${params.controller=='search'?'selected':''}" url="${[controller:'search']}" id="tab-search">Search</g:link>
</li>
