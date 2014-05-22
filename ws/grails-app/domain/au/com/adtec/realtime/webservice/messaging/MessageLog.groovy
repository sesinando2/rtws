package au.com.adtec.realtime.webservice.messaging

import au.com.adtec.realtime.webservice.AbstractLog

class MessageLog extends AbstractLog {

    Message message
    MessageLogAction action
}

enum MessageLogAction { READ, RESPOND }
