package frontlinesms2

enum EmailReceiveProtocol {
	IMAP, IMAPS, POP3, POP3S;
	
	String toString() { return name().toLowerCase() }
}
