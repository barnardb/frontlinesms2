package frontlinesms2

class StatusController {
	def index = {
			def messages = Fmessage.findAll();

			def x = [], y = [], z = [];
			180.times { 
			
			def date = new Date() + it 
			def ds = date.format('dd/MM');
			x << "'${ds}'"
			def random = new Random()
			y << random.nextInt(200-10+1)+10
			z << random.nextInt(200-10+1)+10
			}
			println "${x} ${y} ${z}"
			[messageInstance: messages, xdata: x, y1data: y, y2data: z]
	}
	

}
