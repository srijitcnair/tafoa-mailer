package com.cg.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class Mailer {

	String host = "smtp.mail.yahoo.com";
	String port = "587";

	String emailid = "cgaddressownersgroup@yahoo.com";
	String userid = "cgaddressownersgroup";
	String password = "$Sooperpwd12#";
	Properties properties = System.getProperties();

	Session session = null;
	final String messageTemplate = "<p>Hello %s,<br>"			
			+ "<p>Please  find attached <b>Circular 002/2017</b> which contains important information regarding PG Hostel Accomodation</p>"
			
			+ "<p>If you have any queries please revert to us at tafoa-ec@googlegroups.com</p>"
			
			+ "<br><br>Thanks<br> TAFOA Executive Committee";
	final String title = "Notice to all owners and residents: Circular 002/2017";

	public Mailer() {
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.ssl.trust", "smtp.mail.yahoo.com");
		properties.put("mail.smtp.host", host);
	}

	public void sendMail() {
		try {

			Scanner sc = new Scanner(new File("C:\\Our Data\\Our Address\\Association\\mailer\\cgemails.txt"));

			List<String[]> data = new ArrayList<String[]>();
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				String[] fields = line.split("\\|");
				if (fields != null && fields.length == 3 && fields[0].trim().length() > 0
						&& !fields[0].startsWith("#")
						&& fields[1].trim().length() > 0 && fields[2].trim().length() > 0) {
					data.add(fields);
				} else {
					System.err.println("Line omitted:" + line);
				}

			}

			int count = 1;
			int loopCount = 0;
			Transport transport = null;

			for (String[] sa : data) {

				if (loopCount == 0) {
					session = session.getDefaultInstance(properties);
					transport = session.getTransport("smtp");
					System.out.println("Creating a new connection");
					transport.connect(host, userid, password);
					loopCount = 1;
				}

				String aptNum = sa[0].trim();
				String name = sa[1].trim();
				String toEmailId = sa[2].trim();
				// String url = sa[5].trim();
				String[] attachments = new String[] {
						  "C:\\Our Data\\Our Address\\Association\\mailer\\Circular No. 002 - 2017.pdf"
						};

				System.out.println(count + ":Sending email to apt:" + aptNum);
				sendMessage(transport, toEmailId, title, String.format(messageTemplate, name, aptNum), attachments);

				count++;
				loopCount++;

				if (loopCount == 5) {
					transport.close();
					loopCount = 0;
				}

				Thread.sleep(5000);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addAttachment(Multipart multipart, String filename) throws Exception {
		DataSource source = new FileDataSource(filename);
		BodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setDataHandler(new DataHandler(source));
		
		String displayName = filename.substring(filename.lastIndexOf('\\')+1);
		
		messageBodyPart.setFileName(displayName);
		multipart.addBodyPart(messageBodyPart);
	}

	public boolean sendMessage(Transport transport, String toEmail, String subject, String msg, String[] attachments) {
		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(this.emailid, "TAFOA - Executive Committee"));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
			message.setSubject(subject);
			message.setHeader("X-Mailer", "SMTPSend");
			message.setSentDate(new Date());

			Multipart multipart = new MimeMultipart();

			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent(msg, "text/html");
			multipart.addBodyPart(messageBodyPart);

			if (attachments != null && attachments.length > 0) {
				for (String attachment : attachments) {
					addAttachment(multipart, attachment);
				}
			}

			message.setContent(multipart);
			transport.sendMessage(message, message.getAllRecipients());

		} catch (MessagingException mex) {
			mex.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	/*
	 * private List<String[]> getData1() { List<String[]> data = new
	 * ArrayList<String[]>();
	 * 
	 * data.add(new String[] { "C3619744-136272", "36", "C202        ",
	 * "Suhasini Ganesan", "srijitcnair@gmail.com",
	 * "http://tinyurl.com/y9tykjkt",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=C3619744-136272&entry.553102638=C202&entry.678784906=Suhasini Ganesan&id=vr3hf"
	 * , "" }); // data.add(new String[] {"E6012340-119238","60","E405 ","Dr. //
	 * Rathan","gkrathnavel@gmail.com","http://tinyurl.com/yar6yks3","https://
	 * docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-
	 * uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.
	 * 1443148247=E6012340-119238&entry.553102638=E405&entry.678784906=Dr. //
	 * Rathan&id=vr3hf",""}); // data.add(new String[]
	 * {"D4414808-130594","44","D304 //
	 * ","Dr.Rajendran","doc.rajendran@gmail.com","http://tinyurl.com/
	 * y76ybn87","https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-
	 * uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.
	 * 1443148247=D4414808-130594&entry.553102638=D304&entry.678784906=Dr.
	 * Rajendran&id=vr3hf",""});
	 * 
	 * return data; }
	 * 
	 * private List<String[]> getData() { List<String[]> data = new
	 * ArrayList<String[]>();
	 * 
	 * data.add(new String[] { "A122212-136272", "1", "A102        ",
	 * "Srinivasa Varadhan", "rsvaradhan2000@yahoo.com",
	 * "http://tinyurl.com/y7jmaloo",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=A122212-136272&entry.553102638=A102&entry.678784906=Srinivasa Varadhan&id=vr3hf"
	 * , "" }); data.add(new String[] { "A24936-113560", "2", "A104        ",
	 * "Ravi", "r_shankar1@yahoo.com", "http://tinyurl.com/ybcnxooy",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=A24936-113560&entry.553102638=A104&entry.678784906=Ravi&id=vr3hf",
	 * "" }); data.add(new String[] { "A37404-113560", "3", "A202        ",
	 * "Victor", "victormano@yahoo.com", "http://tinyurl.com/y6vvyg46",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=A37404-113560&entry.553102638=A202&entry.678784906=Victor&id=vr3hf",
	 * "" }); data.add(new String[] { "A411106-90848", "4", "A204        ",
	 * "Jayaraman", "rjayr1@gmail.com", "http://tinyurl.com/ya4hu2gg",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=A411106-90848&entry.553102638=A204&entry.678784906=Jayaraman&id=vr3hf",
	 * "" }); data.add(new String[] { "A591316-181696", "5", "A302        ",
	 * "PARANEETHARAN MARIMUTHU,Ms.Sangeetha,Mr.T.N Rengaraj & Mrs.Rani Rengaraj"
	 * , "marimuthuparaneetharan@yahoo.com", "http://tinyurl.com/ydyzrlc3",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=A591316-181696&entry.553102638=A302&entry.678784906=PARANEETHARAN MARIMUTHU,Ms.Sangeetha,Mr.T.N Rengaraj & Mrs.Rani Rengaraj&id=vr3hf"
	 * , "" }); data.add(new String[] { "A66170-102204", "6", "A304        ",
	 * "Mohan", "mkompala@gmail.com", "http://tinyurl.com/y9f88rj9",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=A66170-102204&entry.553102638=A304&entry.678784906=Mohan&id=vr3hf",
	 * "" }); data.add(new String[] { "A722212-124916", "7", "A403        ",
	 * "Meenakshi Prabakar", "opalhouse.mp@gmail.com",
	 * "http://tinyurl.com/y8yvcveu",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=A722212-124916&entry.553102638=A403&entry.678784906=Meenakshi Prabakar&id=vr3hf"
	 * , "" }); data.add(new String[] { "A820978-130594", "8", "A103        ",
	 * "GANESH SETHURAMAN", "ganeshmailbox@gmail.com",
	 * "http://tinyurl.com/ybvzg6yc",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=A820978-130594&entry.553102638=A103&entry.678784906=GANESH SETHURAMAN&id=vr3hf"
	 * , "" }); data.add(new String[] { "A912340-107882", "9", "A201        ",
	 * "S Karthika", "senthil16@gmail.com", "http://tinyurl.com/y8tcqd8v",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=A912340-107882&entry.553102638=A201&entry.678784906=S Karthika&id=vr3hf"
	 * , "" }); data.add(new String[] { "A1016042-119238", "10", "A203        ",
	 * "Ramesh kannan", "yourrameshk@gmail.com", "http://tinyurl.com/y7mrodr7",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=A1016042-119238&entry.553102638=A203&entry.678784906=Ramesh kannan&id=vr3hf"
	 * , "" }); data.add(new String[] { "A1127148-107882", "11", "A301        ",
	 * "Meera Manohar Krishnan", "manokrish@gmail.com",
	 * "http://tinyurl.com/y8jsxw35",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=A1127148-107882&entry.553102638=A301&entry.678784906=Meera Manohar Krishnan&id=vr3hf"
	 * , "" }); data.add(new String[] { "A1213574-130594", "12", "A303        ",
	 * "Kuhu Biswas", "biswashirak@yahoo.co.in", "http://tinyurl.com/y9pe8hsq",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=A1213574-130594&entry.553102638=A303&entry.678784906=Kuhu Biswas&id=vr3hf"
	 * , "" }); data.add(new String[] { "A1319744-107882", "13", "A402        ",
	 * "SHEEBA NATARAJAN", "sheebanat@gmail.com", "http://tinyurl.com/yd35tb4u",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=A1319744-107882&entry.553102638=A402&entry.678784906=SHEEBA NATARAJAN&id=vr3hf"
	 * , "" }); data.add(new String[] { "B1416042-136272", "14", "B202        ",
	 * "Rashi Chachra", "chachra.gaurav@gmail.com",
	 * "http://tinyurl.com/y83zel7o",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=B1416042-136272&entry.553102638=B202&entry.678784906=Rashi Chachra&id=vr3hf"
	 * , "" }); data.add(new String[] { "B1516042-141950", "15", "B203        ",
	 * "Anirban Ghosh", "anirbanghoshcpa@gmail.com",
	 * "http://tinyurl.com/ybea2egs",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=B1516042-141950&entry.553102638=B203&entry.678784906=Anirban Ghosh&id=vr3hf"
	 * , "" }); data.add(new String[] { "B1616042-119238", "16", "B101        ",
	 * "SHANTHI RAO C", "shanthicrao@gmail.com", "http://tinyurl.com/ycmf63od",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=B1616042-119238&entry.553102638=B101&entry.678784906=SHANTHI RAO C&id=vr3hf"
	 * , "" }); data.add(new String[] { "B1717276-136272", "17", "B102        ",
	 * "V.BHUVANESWARI", "venkat_chand@hotmail.com",
	 * "http://tinyurl.com/ybxjc73b",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=B1717276-136272&entry.553102638=B102&entry.678784906=V.BHUVANESWARI&id=vr3hf",
	 * "" }); data.add(new String[] { "B1819744-96526", "18", "B103        ",
	 * "Mohanakrishnan N", "mokrish@gmail.com", "http://tinyurl.com/y9bnzaf9",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=B1819744-96526&entry.553102638=B103&entry.678784906=Mohanakrishnan N&id=vr3hf"
	 * , "" }); data.add(new String[] { "B1937020-102204", "19", "B104        ",
	 * "Suresh Kumar Panchpakesan Iyer", "psk10100@gmail.com",
	 * "http://tinyurl.com/y8h94sl8",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=B1937020-102204&entry.553102638=B104&entry.678784906=Suresh Kumar Panchpakesan Iyer&id=vr3hf"
	 * , "" }); data.add(new String[] { "B2024680-124916", "20", "B201        ",
	 * "Manoranjan Thangaraj", "manoranjan.t@gmail.com",
	 * "http://tinyurl.com/y9zsktbc",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=B2024680-124916&entry.553102638=B201&entry.678784906=Manoranjan Thangaraj&id=vr3hf"
	 * , "" }); data.add(new String[] { "B2125914-107882", "21", "B204        ",
	 * "Pradeep Rao Padamnoor", "padamnoor@gmail.com",
	 * "http://tinyurl.com/yc25s7qf",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=B2125914-107882&entry.553102638=B204&entry.678784906=Pradeep Rao Padamnoor&id=vr3hf"
	 * , "" }); data.add(new String[] { "B2214808-113560", "22", "B301        ",
	 * "Baskar Arasu", "drcpsekhar@yahoo.com", "http://tinyurl.com/y954ulus",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=B2214808-113560&entry.553102638=B301&entry.678784906=Baskar Arasu&id=vr3hf"
	 * , "" }); data.add(new String[] { "B2320978-147628", "23", "B302        ",
	 * "Chitra Loganathan", "retnamloganathan@gmail.com",
	 * "http://tinyurl.com/y8oe7ehb",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=B2320978-147628&entry.553102638=B302&entry.678784906=Chitra Loganathan&id=vr3hf"
	 * , "" }); data.add(new String[] { "B2412340-96526", "24", "B303        ",
	 * "V S M Raju", "rajuvsm@gmail.com", "http://tinyurl.com/y8hzsraj",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=B2412340-96526&entry.553102638=B303&entry.678784906=V S M Raju&id=vr3hf"
	 * , "" }); data.add(new String[] { "B2516042-141950", "25", "B304        ",
	 * "SMITHA RAMESH", "smitha_ramesh24@yahoo.com",
	 * "http://tinyurl.com/yctk8mkg",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=B2516042-141950&entry.553102638=B304&entry.678784906=SMITHA RAMESH&id=vr3hf"
	 * , "" }); data.add(new String[] { "B2622212-119238", "26", "B401        ",
	 * "SANKARANARAYANAN S", "sankarworks@gmail.com",
	 * "http://tinyurl.com/yb7oepwe",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=B2622212-119238&entry.553102638=B401&entry.678784906=SANKARANARAYANAN S&id=vr3hf"
	 * , "" }); data.add(new String[] { "B2723446-102204", "27", "B402        ",
	 * "ASHWIN SAKTHI RAM S", "magsuren@yahoo.com",
	 * "http://tinyurl.com/y9wjb3ot",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=B2723446-102204&entry.553102638=B402&entry.678784906=ASHWIN SAKTHI RAM S&id=vr3hf"
	 * , "" }); data.add(new String[] { "C2818510-147628", "28", "C201        ",
	 * "BALAJEE KARTHIK", "balajee.karthick@gmail.com",
	 * "http://tinyurl.com/yd3fk9r8",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=C2818510-147628&entry.553102638=C201&entry.678784906=BALAJEE KARTHIK&id=vr3hf"
	 * , "" }); data.add(new String[] { "C2918510-102204", "29", "C204        ",
	 * "Arun KUMAR Nair", "pknair99@gmail.com", "http://tinyurl.com/yblf5xcv",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=C2918510-102204&entry.553102638=C204&entry.678784906=Arun KUMAR Nair&id=vr3hf"
	 * , "" }); data.add(new String[] { "C3019744-107882", "30", "C302        ",
	 * "Dr. Julius Scott", "jxscott@hotmail.com", "http://tinyurl.com/y7g93p9q",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=C3019744-107882&entry.553102638=C302&entry.678784906=Dr. Julius Scott&id=vr3hf"
	 * , "" }); data.add(new String[] { "C319872-107882", "31", "C101        ",
	 * "J Harvey", "j.harveey@gmail.com", "http://tinyurl.com/ydaavuyx",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=C319872-107882&entry.553102638=C101&entry.678784906=J Harvey&id=vr3hf"
	 * , "" }); data.add(new String[] { "C3218510-136272", "32", "C102        ",
	 * "S Muthukaruppan", "muthukaruppans@gmail.com",
	 * "http://tinyurl.com/yc5ggat7",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=C3218510-136272&entry.553102638=C102&entry.678784906=S Muthukaruppan&id=vr3hf"
	 * , "" }); data.add(new String[] { "C3313574-158984", "33", "C103        ",
	 * "B.SIVAKUMAR", "sivakumar_nalini@yahoo.co.in",
	 * "http://tinyurl.com/y95ycvdo",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=C3313574-158984&entry.553102638=C103&entry.678784906=B.SIVAKUMAR&id=vr3hf",
	 * "" }); data.add(new String[] { "C3419744-107882", "34", "C104        ",
	 * "Dr R Uma Shankar", "drus007@hotmail.com", "http://tinyurl.com/yagkme85",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=C3419744-107882&entry.553102638=C104&entry.678784906=Dr R Uma Shankar&id=vr3hf"
	 * , "" }); data.add(new String[] { "C3516042-158984", "35", "C201        ",
	 * "N Subramanian", "er.subramanian1952@gmail.com",
	 * "http://tinyurl.com/y75tykwy",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=C3516042-158984&entry.553102638=C201&entry.678784906=N Subramanian&id=vr3hf"
	 * , "" }); data.add(new String[] { "C3619744-136272", "36", "C202        ",
	 * "Suhasini Ganesan", "suhasinisrijit@gmail.com",
	 * "http://tinyurl.com/y9tykjkt",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=C3619744-136272&entry.553102638=C202&entry.678784906=Suhasini Ganesan&id=vr3hf"
	 * , "" }); data.add(new String[] { "C3724680-124916", "37", "C203        ",
	 * "SUNNY SANTHOSH KUMAR", "sunnysk_1999@yahoo.com",
	 * "http://tinyurl.com/y8roshk3",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=C3724680-124916&entry.553102638=C203&entry.678784906=SUNNY SANTHOSH KUMAR&id=vr3hf"
	 * , "" }); data.add(new String[] { "C3824680-102204", "38", "C301        ",
	 * "SURESH BABU NAVULURI", "navuluri@yahoo.com",
	 * "http://tinyurl.com/y9m68we4",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=C3824680-102204&entry.553102638=C301&entry.678784906=SURESH BABU NAVULURI&id=vr3hf"
	 * , "" }); data.add(new String[] { "C3913574-107882", "39", "C303        ",
	 * "Bharadhwaaj", "lb_waaj@hotmail.com", "http://tinyurl.com/y875qmwr",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=C3913574-107882&entry.553102638=C303&entry.678784906=Bharadhwaaj&id=vr3hf",
	 * "" }); data.add(new String[] { "C4028382-124916", "40", "C304        ",
	 * "Veerappan Udaiyar Sekar", "sekarudaiyar@gmail.com",
	 * "http://tinyurl.com/ycjcgkzu",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=C4028382-124916&entry.553102638=C304&entry.678784906=Veerappan Udaiyar Sekar&id=vr3hf"
	 * , "" }); data.add(new String[] { "D4138254-119238", "41", "D102        ",
	 * "S G Prasad &amp; Radhika Prasad", "prasadhikka@gmail.com",
	 * "http://tinyurl.com/y98ksr9q",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=D4138254-119238&entry.553102638=D102&entry.678784906=S G Prasad &amp; Radhika Prasad&id=vr3hf"
	 * , "" }); data.add(new String[] { "D4216042-107882", "42", "D104        ",
	 * "V Ashok Kumar", "ashoksadu@gmail.com", "http://tinyurl.com/y7jpyulo",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=D4216042-107882&entry.553102638=D104&entry.678784906=V Ashok Kumar&id=vr3hf"
	 * , "" }); data.add(new String[] { "D4327148-102204", "43", "D203        ",
	 * "K. C. Sathiyanarayanan", "kcsathya@yahoo.com",
	 * "http://tinyurl.com/ybb7cvbc",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=D4327148-102204&entry.553102638=D203&entry.678784906=K. C. Sathiyanarayanan&id=vr3hf"
	 * , "" }); // data.add(new String[] {"D4414808-130594","44","D304 //
	 * ","Dr.Rajendran","doc.rajendran@gmail.com","http://tinyurl.com/
	 * y76ybn87","https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-
	 * uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.
	 * 1443148247=D4414808-130594&entry.553102638=D304&entry.678784906=Dr.
	 * Rajendran&id=vr3hf",""}); data.add(new String[] { "D4517276-107882",
	 * "45", "D101        ", "Preeti cherian", "cpreeti29@gmail.com",
	 * "http://tinyurl.com/y8ru5jt9",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=D4517276-107882&entry.553102638=D101&entry.678784906=Preeti cherian&id=vr3hf"
	 * , "" }); data.add(new String[] { "D4634552-136272", "46", "D103        ",
	 * "Selvakumar Meenakshisundaram", "drajendran1952@gmail.com",
	 * "http://tinyurl.com/y8e6fcfp",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=D4634552-136272&entry.553102638=D103&entry.678784906=Selvakumar Meenakshisundaram&id=vr3hf"
	 * , "" }); data.add(new String[] { "D4732084-130594", "47", "D201        ",
	 * "Narasimhan Balasubramanian", "narsibvl_2006@yahoo.com",
	 * "http://tinyurl.com/ydx36rwe",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=D4732084-130594&entry.553102638=D201&entry.678784906=Narasimhan Balasubramanian&id=vr3hf"
	 * , "" }); data.add(new String[] { "D4813574-102204", "48", "D202        ",
	 * "P DEVAPIRAN", "devpiran@gmail.com", "http://tinyurl.com/y8xfp528",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=D4813574-102204&entry.553102638=D202&entry.678784906=P DEVAPIRAN&id=vr3hf"
	 * , "" }); data.add(new String[] { "D4911106-124916", "49", "D204        ",
	 * "Sukumar V", "sukumarfirst@gmail.com", "http://tinyurl.com/yaugmnus",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=D4911106-124916&entry.553102638=D204&entry.678784906=Sukumar V&id=vr3hf"
	 * , "" }); data.add(new String[] { "D5012340-102204", "50", "D302        ",
	 * "Krishnan N", "nk231088@gmail.com", "http://tinyurl.com/y8ojhgqh",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=D5012340-102204&entry.553102638=D302&entry.678784906=Krishnan N&id=vr3hf"
	 * , "" }); data.add(new String[] { "D5117276-141950", "51", "D303        ",
	 * "Gopalakrishnan", "gopaalakrishnan@gmail.com",
	 * "http://tinyurl.com/ydhbkjgv",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=D5117276-141950&entry.553102638=D303&entry.678784906=Gopalakrishnan&id=vr3hf",
	 * "" }); data.add(new String[] { "D5216042-113560", "52", "D304        ",
	 * "Dr. Meenakshi", "dr.meenu83@gmail.com", "http://tinyurl.com/ycpg384r",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=D5216042-113560&entry.553102638=D304&entry.678784906=Dr. Meenakshi&id=vr3hf"
	 * , "" }); data.add(new String[] { "D5311106-141950", "53", "D402        ",
	 * "R SHANTHA", "shanta_nanu85@yahoo.co.in", "http://tinyurl.com/yc2b5a2l",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=D5311106-141950&entry.553102638=D402&entry.678784906=R SHANTHA&id=vr3hf"
	 * , "" }); data.add(new String[] { "D549872-158984", "54", "D403        ",
	 * "RAMESH M", "ramesh.k.muthusamy@gmail.com",
	 * "http://tinyurl.com/y72nnk5f",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=D549872-158984&entry.553102638=D403&entry.678784906=RAMESH M&id=vr3hf"
	 * , "" }); data.add(new String[] { "E5514808-113560", "55", "E101        ",
	 * "Ravichandran", "s.b.ravi53@gmail.com", "http://tinyurl.com/ycgyshg5",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=E5514808-113560&entry.553102638=E101&entry.678784906=Ravichandran&id=vr3hf",
	 * "" }); data.add(new String[] { "E5614808-102204", "56", "E202        ",
	 * "Muralidharan", "mur12721@gmail.com", "http://tinyurl.com/ybep3ffc",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=E5614808-102204&entry.553102638=E202&entry.678784906=Muralidharan&id=vr3hf",
	 * "" }); data.add(new String[] { "E577404-90848", "57", "E303        ",
	 * "Sowmya", "sowmyb@yahoo.com", "http://tinyurl.com/y8ka2xym",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=E577404-90848&entry.553102638=E303&entry.678784906=Sowmya&id=vr3hf",
	 * "" }); data.add(new String[] { "E588638-102204", "58", "E304        ",
	 * "Jyotsna", "kjyotsna@gmail.com", "http://tinyurl.com/ya35qwkv",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=E588638-102204&entry.553102638=E304&entry.678784906=Jyotsna&id=vr3hf",
	 * "" }); data.add(new String[] { "E5911106-79492", "59", "E304        ",
	 * "Venkatesh", "vbi4@yahoo.com", "http://tinyurl.com/ybpykt9x",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=E5911106-79492&entry.553102638=E304&entry.678784906=Venkatesh&id=vr3hf",
	 * "" }); // data.add(new String[] {"E6012340-119238","60","E405 ","Dr. //
	 * Rathan","gkrathnavel@gmail.com","http://tinyurl.com/yar6yks3","https://
	 * docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-
	 * uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.
	 * 1443148247=E6012340-119238&entry.553102638=E405&entry.678784906=Dr. //
	 * Rathan&id=vr3hf",""}); data.add(new String[] { "E617404-124916", "61",
	 * "E405        ", "Malini", "malinirathan@gmail.com",
	 * "http://tinyurl.com/ya6jwq7r",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=E617404-124916&entry.553102638=E405&entry.678784906=Malini&id=vr3hf",
	 * "" }); data.add(new String[] { "E6229616-90848", "62", "E103        ",
	 * "VAITHIANATHAN MAGHADEVAN", "mdevan@gmail.com",
	 * "http://tinyurl.com/ydduepu2",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=E6229616-90848&entry.553102638=E103&entry.678784906=VAITHIANATHAN MAGHADEVAN&id=vr3hf"
	 * , "" }); data.add(new String[] { "E6317276-73814", "63", "E104        ",
	 * "R Muralidharan", "murali@mpl.in", "http://tinyurl.com/yadfbdej",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=E6317276-73814&entry.553102638=E104&entry.678784906=R Muralidharan&id=vr3hf"
	 * , "" }); data.add(new String[] { "E6434552-107882", "64", "E201        ",
	 * "Prakash Krishna Moorthi Iyer", "prmoorthi@gmail.com",
	 * "http://tinyurl.com/yctpzotm",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=E6434552-107882&entry.553102638=E201&entry.678784906=Prakash Krishna Moorthi Iyer&id=vr3hf"
	 * , "" }); data.add(new String[] { "E6525914-164662", "65", "E203        ",
	 * "Natesh P Parameswaran", "natesh.parameswaran@gmail.com",
	 * "http://tinyurl.com/yc4q23zu",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=E6525914-164662&entry.553102638=E203&entry.678784906=Natesh P Parameswaran&id=vr3hf"
	 * , "" }); data.add(new String[] { "E6614808-90848", "66", "E302        ",
	 * "Saslin Salim", "saslin@gmail.com", "http://tinyurl.com/y9xtpugz",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=E6614808-90848&entry.553102638=E302&entry.678784906=Saslin Salim&id=vr3hf"
	 * , "" }); data.add(new String[] { "E6718510-119238", "67", "E402        ",
	 * "VIMALA GEETHA K", "drphanibabu@gmail.com",
	 * "http://tinyurl.com/y7wkxsfp",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=E6718510-119238&entry.553102638=E402&entry.678784906=VIMALA GEETHA K&id=vr3hf"
	 * , "" }); data.add(new String[] { "E6812340-119238", "68", "E403        ",
	 * "Harini Rao", "hariniashok@gmail.com", "http://tinyurl.com/y755kdlo",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=E6812340-119238&entry.553102638=E403&entry.678784906=Harini Rao&id=vr3hf"
	 * , "" }); data.add(new String[] { "F697404-107882", "69", "F203        ",
	 * "Laxman", "laravazhi@yahoo.com", "http://tinyurl.com/y7yoekys",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=F697404-107882&entry.553102638=F203&entry.678784906=Laxman&id=vr3hf",
	 * "" }); data.add(new String[] { "F709872-102204", "70", "F301        ",
	 * "Abhishek", "mishrabh@yahoo.com", "http://tinyurl.com/y86fcfmh",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=F709872-102204&entry.553102638=F301&entry.678784906=Abhishek&id=vr3hf",
	 * "" }); data.add(new String[] { "F7118510-96526", "71", "F304        ",
	 * "P. A Srinivasan", "pasrini@gmail.com", "http://tinyurl.com/y8ryea9q",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=F7118510-96526&entry.553102638=F304&entry.678784906=P. A Srinivasan&id=vr3hf"
	 * , "" }); data.add(new String[] { "F7233318-119238", "72", "F403        ",
	 * "Vijayalakshmi Balaji Chinni", "shankar.aar@gmail.com",
	 * "http://tinyurl.com/yd62r5jn",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=F7233318-119238&entry.553102638=F403&entry.678784906=Vijayalakshmi Balaji Chinni&id=vr3hf"
	 * , "" }); data.add(new String[] { "F7313574-130594", "73", "F101        ",
	 * "ALAMELU M K", "akilaparvathy@yahoo.com", "http://tinyurl.com/ycbldnpm",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=F7313574-130594&entry.553102638=F101&entry.678784906=ALAMELU M K&id=vr3hf"
	 * , "" }); data.add(new String[] { "F7418510-113560", "74", "F102        ",
	 * "K. Jayachandran", "jaya050845@yahoo.com", "http://tinyurl.com/ybo2bt5d",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=F7418510-113560&entry.553102638=F102&entry.678784906=K. Jayachandran&id=vr3hf"
	 * , "" }); data.add(new String[] { "F7532084-141950", "75", "F103        ",
	 * "Hemalatha Lakshminarayanan", "lak.narayanan.r@gmail.com",
	 * "http://tinyurl.com/ya26hwhb",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=F7532084-141950&entry.553102638=F103&entry.678784906=Hemalatha Lakshminarayanan&id=vr3hf"
	 * , "" }); data.add(new String[] { "F7617276-124916", "76", "F104        ",
	 * "Gladys William", "gladywilliam@gmail.com",
	 * "http://tinyurl.com/ybh7x8vl",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=F7617276-124916&entry.553102638=F104&entry.678784906=Gladys William&id=vr3hf"
	 * , "" }); data.add(new String[] { "F7740722-141950", "77", "F201        ",
	 * "Jayachandran deepak balasubramian", "deepakiimm@rediffmail.com",
	 * "http://tinyurl.com/yczgpawe",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=F7740722-141950&entry.553102638=F201&entry.678784906=Jayachandran deepak balasubramian&id=vr3hf"
	 * , "" }); data.add(new String[] { "F7813574-124916", "78", "F202        ",
	 * "RAMANUJAM R", "ramanujamr50@gmail.com", "http://tinyurl.com/yafu86us",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=F7813574-124916&entry.553102638=F202&entry.678784906=RAMANUJAM R&id=vr3hf"
	 * , "" }); data.add(new String[] { "F7916042-130594", "79", "F204        ",
	 * "V. Prashanthi", "prashanthivvs@gmail.com",
	 * "http://tinyurl.com/yacg2daw",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=F7916042-130594&entry.553102638=F204&entry.678784906=V. Prashanthi&id=vr3hf"
	 * , "" }); data.add(new String[] { "F8011106-136272", "80", "F302        ",
	 * "R. Anandh", "rajappa.anandh@gmail.com", "http://tinyurl.com/y9aezbmb",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=F8011106-136272&entry.553102638=F302&entry.678784906=R. Anandh&id=vr3hf"
	 * , "" }); data.add(new String[] { "F8114808-102204", "81", "F303        ",
	 * "K Raja Kumar", "rkumark9@gmail.com", "http://tinyurl.com/y8e9zgzc",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=F8114808-102204&entry.553102638=F303&entry.678784906=K Raja Kumar&id=vr3hf"
	 * , "" }); data.add(new String[] { "F8220978-90848", "82", "F401        ",
	 * "NATHAMUNI BASHYAM", "sinbbn@gmail.com", "http://tinyurl.com/y72n9xnv",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=F8220978-90848&entry.553102638=F401&entry.678784906=NATHAMUNI BASHYAM&id=vr3hf"
	 * , "" }); data.add(new String[] { "F8316042-141950", "83", "F402        ",
	 * "RAM SWAROOP K", "ramswaroop_2001@yahoo.com",
	 * "http://tinyurl.com/ybvthuy8",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=F8316042-141950&entry.553102638=F402&entry.678784906=RAM SWAROOP K&id=vr3hf"
	 * , "" }); data.add(new String[] { "G8413574-113560", "84", "G103        ",
	 * "V SRIVATSAN", "srivatsan2@yahoo.com", "http://tinyurl.com/ycjfjolh",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=G8413574-113560&entry.553102638=G103&entry.678784906=V SRIVATSAN&id=vr3hf"
	 * , "" }); data.add(new String[] { "G8522212-102204", "85", "G302        ",
	 * "GOPALAKRISHNAN S S", "gopal5us@yahoo.com",
	 * "http://tinyurl.com/y7js5yob",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=G8522212-102204&entry.553102638=G302&entry.678784906=GOPALAKRISHNAN S S&id=vr3hf"
	 * , "" }); data.add(new String[] { "S8630850-130594", "86", "S203 & S204 ",
	 * "Thangaprabhu Chandramohan", "thanga_prabhu@yahoo.com",
	 * "http://tinyurl.com/ybpy69rg",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=S8630850-130594&entry.553102638=S203 & S204&entry.678784906=Thangaprabhu Chandramohan&id=vr3hf"
	 * , "" }); data.add(new String[] { "S8723446-102204", "87", "S101        ",
	 * "Arunkumar Ilangovan", "arunilan@gmail.com",
	 * "http://tinyurl.com/y9fjazj5",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=S8723446-102204&entry.553102638=S101&entry.678784906=Arunkumar Ilangovan&id=vr3hf"
	 * , "" }); data.add(new String[] { "S8823446-124916", "88", "S102        ",
	 * "SAYEED SIDDIQUE M N", "mns_siddique@yahoo.com",
	 * "http://tinyurl.com/y9yskb5l",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=S8823446-124916&entry.553102638=S102&entry.678784906=SAYEED SIDDIQUE M N&id=vr3hf"
	 * , "" }); data.add(new String[] { "S8922212-147628", "89", "S103        ",
	 * "Rachel Mahalakshmi", "rachelmahalakshmi@yahoo.in",
	 * "http://tinyurl.com/ycqavbs9",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=S8922212-147628&entry.553102638=S103&entry.678784906=Rachel Mahalakshmi&id=vr3hf"
	 * , "" }); data.add(new String[] { "S906170-102204", "90", "S104        ",
	 * "Kamya", "kamyaa28@gmail.com", "http://tinyurl.com/y8xzs94s",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=S906170-102204&entry.553102638=S104&entry.678784906=Kamya&id=vr3hf",
	 * "" }); data.add(new String[] { "S9113574-113560", "91", "S105        ",
	 * "Dhananjay J", "jayj300190@gmail.com", "http://tinyurl.com/ya35cd6e",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=S9113574-113560&entry.553102638=S105&entry.678784906=Dhananjay J&id=vr3hf"
	 * , "" }); data.add(new String[] { "S9217276-102204", "92", "S106        ",
	 * "K. Karthikeyan", "carthikk@gmail.com", "http://tinyurl.com/y8gx4tsa",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=S9217276-102204&entry.553102638=S106&entry.678784906=K. Karthikeyan&id=vr3hf"
	 * , "" }); data.add(new String[] { "S9314808-113560", "93", "S108        ",
	 * "P SELVAKUMAR", "selva_jj@yahoo.co.in", "http://tinyurl.com/y9mem5pu",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=S9314808-113560&entry.553102638=S108&entry.678784906=P SELVAKUMAR&id=vr3hf"
	 * , "" }); data.add(new String[] { "S9414808-164662", "94", "S109        ",
	 * "SUMANT ANAND", "krishnavideos2000@yahoo.co.in",
	 * "http://tinyurl.com/ydx69w2y",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=S9414808-164662&entry.553102638=S109&entry.678784906=SUMANT ANAND&id=vr3hf"
	 * , "" }); data.add(new String[] { "S9514808-124916", "95", "S110 & S111 ",
	 * "SEKAR ASHOKA", "sekar.ashoka@gmail.com", "http://tinyurl.com/yanoe7qn",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=S9514808-124916&entry.553102638=S110 & S111&entry.678784906=SEKAR ASHOKA&id=vr3hf"
	 * , "" }); data.add(new String[] { "S9622212-102204", "96", "S112        ",
	 * "Brahannayaki Gopal", "gopalv5a@gmail.com",
	 * "http://tinyurl.com/ycck84ep",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=S9622212-102204&entry.553102638=S112&entry.678784906=Brahannayaki Gopal&id=vr3hf"
	 * , "" }); data.add(new String[] { "S9718510-204408", "97", "S201        ",
	 * "N Radhakrishnan", "radhakrishnan.narayanan@indusind.com",
	 * "http://tinyurl.com/ybbag2dw",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=S9718510-204408&entry.553102638=S201&entry.678784906=N Radhakrishnan&id=vr3hf"
	 * , "" }); data.add(new String[] { "S9824680-170340", "98", "S205        ",
	 * "Venkatesan Narayanan", "narayanan.venkatesan@gmail.com",
	 * "http://tinyurl.com/yaaxoxd6",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=S9824680-170340&entry.553102638=S205&entry.678784906=Venkatesan Narayanan&id=vr3hf"
	 * , "" }); data.add(new String[] { "S9913574-164662", "99", "S207 & S208 ",
	 * "S RAMANUJAM", "venkateshindustries@gmail.com",
	 * "http://tinyurl.com/yawac8ad",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=S9913574-164662&entry.553102638=S207 & S208&entry.678784906=S RAMANUJAM&id=vr3hf"
	 * , "" }); data.add(new String[] { "S10019744-130594", "100",
	 * "S209        ", "R. Senthilnathan", "senthil.nathan@basf.com",
	 * "http://tinyurl.com/yde6o54t",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=S10019744-130594&entry.553102638=S209&entry.678784906=R. Senthilnathan&id=vr3hf"
	 * , "" }); data.add(new String[] { "S10117276-102204", "101",
	 * "S210        ", "N Leela Prabhu", "nlprabhu@gmail.com",
	 * "http://tinyurl.com/y8xsj7r6",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=S10117276-102204&entry.553102638=S210&entry.678784906=N Leela Prabhu&id=vr3hf"
	 * , "" }); data.add(new String[] { "S10214808-141950", "102",
	 * "S211        ", "MAYA NAMBIAR", "mayanambiar2007@gmail.com",
	 * "http://tinyurl.com/y8ej9f7n",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=S10214808-141950&entry.553102638=S211&entry.678784906=MAYA NAMBIAR&id=vr3hf"
	 * , "" }); data.add(new String[] { "S10316042-102204", "103",
	 * "S212        ", "Catakam Kumar", "kcatakam@yahoo.com",
	 * "http://tinyurl.com/ycjpanas",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=S10316042-102204&entry.553102638=S212&entry.678784906=Catakam Kumar&id=vr3hf"
	 * , "" }); data.add(new String[] { "S10427148-130594", "104",
	 * "S301 & S302 ", "Nagarajan S Kandaswamy", "nskandasamy@hotmail.com",
	 * "http://tinyurl.com/ydxgxuoc",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=S10427148-130594&entry.553102638=S301 & S302&entry.678784906=Nagarajan S Kandaswamy&id=vr3hf"
	 * , "" }); data.add(new String[] { "S10512340-113560", "105",
	 * "S303        ", "Manju Sree", "mmhere2004@yahoo.com",
	 * "http://tinyurl.com/y8s9wml7",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=S10512340-113560&entry.553102638=S303&entry.678784906=Manju Sree&id=vr3hf"
	 * , "" }); data.add(new String[] { "S10617276-124916", "106",
	 * "S304        ", "Rajendar Menon", "rajenmenen@hotmail.com",
	 * "http://tinyurl.com/yah5qj99",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=S10617276-124916&entry.553102638=S304&entry.678784906=Rajendar Menon&id=vr3hf"
	 * , "" }); data.add(new String[] { "S10716042-113560", "107",
	 * "S306        ", "N VINOD KUMAR", "nkvinodh@yahoo.co.in",
	 * "http://tinyurl.com/y9mod4f8",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=S10716042-113560&entry.553102638=S306&entry.678784906=N VINOD KUMAR&id=vr3hf"
	 * , "" }); data.add(new String[] { "S10816042-113560", "108",
	 * "S307 & S308 ", "M.MEENA PRIYA", "meenas1957@gmail.com",
	 * "http://tinyurl.com/yd7bchv4",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=S10816042-113560&entry.553102638=S307 & S308&entry.678784906=M.MEENA PRIYA&id=vr3hf"
	 * , "" }); data.add(new String[] { "S10917276-79492", "109",
	 * "S309        ", "RAHAMATH NISSA", "anash@asia.com",
	 * "http://tinyurl.com/yadokpmd",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=S10917276-79492&entry.553102638=S309&entry.678784906=RAHAMATH NISSA&id=vr3hf"
	 * , "" }); data.add(new String[] { "S11016042-141950", "110",
	 * "S310        ", "DIWAKAR SINHA", "sinha.diwakar76@gmail.com",
	 * "http://tinyurl.com/yc6ykfub",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=S11016042-141950&entry.553102638=S310&entry.678784906=DIWAKAR SINHA&id=vr3hf"
	 * , "" }); data.add(new String[] { "S11122212-153306", "111",
	 * "S311        ", "S.SIVA SUBRAMANIAN", "ssubramanian.siva@gmail.com",
	 * "http://tinyurl.com/ybbk7y3a",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=S11122212-153306&entry.553102638=S311&entry.678784906=S.SIVA SUBRAMANIAN&id=vr3hf"
	 * , "" }); data.add(new String[] { "S1128638-176018", "112",
	 * "S312        ", "BHADURI", "bhaduri.saravanakumar@gmail.com",
	 * "http://tinyurl.com/y76e5qx5",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=S1128638-176018&entry.553102638=S312&entry.678784906=BHADURI&id=vr3hf",
	 * "" }); data.add(new String[] { "S11359232-141950", "113", "S401        ",
	 * "SURESH PRADEEP STEPHEN &amp; Mrs. KAVITHA SURESH",
	 * "suresh@upstreamtalent.com", "http://tinyurl.com/y9eqmd29",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=S11359232-141950&entry.553102638=S401&entry.678784906=SURESH PRADEEP STEPHEN &amp; Mrs. KAVITHA SURESH&id=vr3hf"
	 * , "" }); data.add(new String[] { "S11462934-136272", "114",
	 * "S402        ", "Ms. Virginia Joan Delisti F &amp; Mr. Venkkatesan R",
	 * "rsvenkkatesan1@gmail.com", "http://tinyurl.com/ybpau8gr",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=S11462934-136272&entry.553102638=S402&entry.678784906=Ms. Virginia Joan Delisti F &amp; Mr. Venkkatesan R&id=vr3hf"
	 * , "" }); data.add(new String[] { "S11534552-90848", "115",
	 * "S403        ", "XAVIER ANANDA RAJ.ANTONY RAJ", "cinxav@gmail.com",
	 * "http://tinyurl.com/ybzfzla2",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=S11534552-90848&entry.553102638=S403&entry.678784906=XAVIER ANANDA RAJ.ANTONY RAJ&id=vr3hf"
	 * , "" }); data.add(new String[] { "S11618510-141950", "116",
	 * "S405        ", "Trayambak Dutta", "dutta.trayambak@gmail.com",
	 * "http://tinyurl.com/ya3f3bsp",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=S11618510-141950&entry.553102638=S405&entry.678784906=Trayambak Dutta&id=vr3hf"
	 * , "" }); data.add(new String[] { "S11714808-96526", "117",
	 * "S406        ", "Charmila D P", "ccharmil@ford.com",
	 * "http://tinyurl.com/y72wwwd9",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=S11714808-96526&entry.553102638=S406&entry.678784906=Charmila D P&id=vr3hf"
	 * , "" }); data.add(new String[] { "S11820978-124916", "118",
	 * "S407        ", "SRIRAMAKRISHNAN S", "srikrish.eee@gmail.com",
	 * "http://tinyurl.com/ycp7nyqy",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=S11820978-124916&entry.553102638=S407&entry.678784906=SRIRAMAKRISHNAN S&id=vr3hf"
	 * , "" }); data.add(new String[] { "S11919744-187374", "119",
	 * "S408        ", "GOPALAKRISHNAN R", "gopalkrishnan.ramaswamy@gmail.com",
	 * "http://tinyurl.com/y7btz4fb",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=S11919744-187374&entry.553102638=S408&entry.678784906=GOPALAKRISHNAN R&id=vr3hf"
	 * , "" }); data.add(new String[] { "S12019744-113560", "120",
	 * "S409        ", "Papireddy Mopuri", "mopuri1616@gmail.com",
	 * "http://tinyurl.com/y8fzpp22",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=S12019744-113560&entry.553102638=S409&entry.678784906=Papireddy Mopuri&id=vr3hf"
	 * , "" }); data.add(new String[] { "S12125914-102204", "121",
	 * "S410        ", "Vijin Mathew Varghese", "vijin777@gmail.com",
	 * "http://tinyurl.com/ydya759z",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=S12125914-102204&entry.553102638=S410&entry.678784906=Vijin Mathew Varghese&id=vr3hf"
	 * , "" }); data.add(new String[] { "S12218510-136272", "122",
	 * "S411        ", "SUNDARRAJAN P G", "pg.sundarrajan@gmail.com",
	 * "http://tinyurl.com/y8h4232r",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=S12218510-136272&entry.553102638=S411&entry.678784906=SUNDARRAJAN P G&id=vr3hf"
	 * , "" }); data.add(new String[] { "S12351828-119238", "123",
	 * "S412        ", "Bharat venkata srinivasa rao pedasanaganti",
	 * "b_v_s_rao@yahoo.co.in", "http://tinyurl.com/ybnuuwp8",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=S12351828-119238&entry.553102638=S412&entry.678784906=Bharat venkata srinivasa rao pedasanaganti&id=vr3hf"
	 * , "" }); data.add(new String[] { "E12419744-102204", "124",
	 * "E201        ", "Kavita / Prakash", "kavitavc@gmail.com",
	 * "http://tinyurl.com/y9beapdd",
	 * "https://docs.google.com/forms/d/e/1FAIpQLScH2mL8ohL-uCzoObZio57DpLmvIJItRPI44TxmPIt-PciIQA/viewform?usp=pp_url&entry.1443148247=E12419744-102204&entry.553102638=E201&entry.678784906=Kavita / Prakash&id=vr3hf"
	 * , "" });
	 * 
	 * return data; }
	 */

	public static void main(String[] args) {
		Mailer m = new Mailer();
		m.sendMail();
	}

}
