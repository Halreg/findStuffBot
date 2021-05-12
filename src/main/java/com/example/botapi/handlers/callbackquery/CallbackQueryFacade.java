package com.example.botapi.handlers.callbackquery;


import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;



@Component
public class CallbackQueryFacade {
    //private ReplyMessagesService messagesService;
    //private List<CallbackQueryHandler> callbackQueryHandlers;
/*
    public CallbackQueryFacade(ReplyMessagesService messagesService,
                               List<CallbackQueryHandler> callbackQueryHandlers) {
        this.messagesService = messagesService;
        this.callbackQueryHandlers = callbackQueryHandlers;
    }
*/
    public SendMessage processCallbackQuery(CallbackQuery usersQuery) {
        /*
        CallbackQueryType usersQueryType = CallbackQueryType.valueOf(usersQuery.getData().split("\\|")[0]);

        Optional<CallbackQueryHandler> queryHandler = callbackQueryHandlers.stream().
                filter(callbackQuery -> callbackQuery.getHandlerQueryType().equals(usersQueryType)).findFirst();

        return queryHandler.map(handler -> handler.handleCallbackQuery(usersQuery)).
                orElse(messagesService.getWarningReplyMessage(usersQuery.getMessage().getChatId(), "reply.query.failed"));

         */
        return new SendMessage(usersQuery.getId(), "calbackQuery");
    }
}
