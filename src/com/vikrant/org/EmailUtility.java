package com.vikrant.org;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;

public class EmailUtility {
	public static String sendMail(String toEmail, String toName,
			String msgText, String subject, String replyTo, String sender) throws IOException {
		LinkedList<String> recipients = new LinkedList<String>();
		if (toName == null || toName.trim().length() == 0) {
			recipients.add(toEmail);
		} else {
			recipients.add(toName + " <" + toEmail + ">");
		}
		return SendMailSES(sender, recipients, subject, msgText, replyTo);
	}
	private static String SendMailSES(String sender,
			LinkedList<String> recipients, String subject, String body,
			String replyTo) throws IOException {
		Destination destination = new Destination(recipients);
		Content subjectContent = new Content(subject);
		Content htmlContent = new Content(body);
		Body msgBody = new Body().withHtml(htmlContent);
		Message msg = new Message(subjectContent, msgBody);
		SendEmailRequest request = new SendEmailRequest(sender, destination,
				msg);
		if (replyTo != null) {
			List<String> replyToList = new ArrayList<String>(2);
			replyToList.add(replyTo);
			request.setReplyToAddresses(replyToList);
		}

		FnyAWSEmailService.addEmail(request);
		FnyAWSEmailService fnyawsemail = new FnyAWSEmailService();
		fnyawsemail.sendMail(request);
		return "SUCCESS";
	}

}
