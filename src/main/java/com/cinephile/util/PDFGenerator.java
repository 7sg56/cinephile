package com.cinephile.util;

import com.cinephile.model.Booking;
import com.cinephile.model.Seat;
import com.cinephile.model.Show;
import com.cinephile.model.User;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.util.List;
import java.util.stream.Collectors;

public class PDFGenerator {

    public static String generateTicketReceipt(User user, Show show, Booking booking, List<Seat> seats,
            String paymentMethod) throws Exception {
        // Output path (saved wherever the user runs the JAR/compiler from)
        String fileName = "Cinephile_Ticket_" + booking.getBookingReference() + ".pdf";

        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(fileName));

        document.open();

        // Fonts
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD);
        Font boldFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
        Font normalFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);

        // Header
        Paragraph title = new Paragraph("CINEPHILE TICKETS", titleFont);
        title.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph("\n"));

        // User Info
        document.add(new Paragraph("Customer: " + user.getFullName(), normalFont));
        document.add(new Paragraph("Booking Reference: " + booking.getBookingReference(), boldFont));
        document.add(new Paragraph("\n--------------------------------------------------------------\n"));

        // Movie & Show Details
        document.add(new Paragraph("Movie: " + show.getMovieTitle(), boldFont));
        document.add(new Paragraph("Date & Time: " + show.getShowDate() + " | " + show.getShowTime(), normalFont));
        document.add(new Paragraph("Hall: " + show.getHallName(), normalFont));

        // Seat Data
        String seatNumbers = seats.stream().map(Seat::getSeatNumber).collect(Collectors.joining(", "));
        document.add(new Paragraph("Total Tickets: " + booking.getTotalTickets(), normalFont));
        document.add(new Paragraph("Assigned Seats: " + seatNumbers, boldFont));
        document.add(new Paragraph("\n--------------------------------------------------------------\n"));

        // Receipt Summary
        document.add(new Paragraph("Tickets Subtotal: $" + booking.getTicketsSubtotal(), normalFont));
        document.add(new Paragraph("Food & Amenities: $" + booking.getFoodSubtotal(), normalFont));
        document.add(new Paragraph("Taxes Applied: $" + booking.getTaxes(), normalFont));
        document.add(new Paragraph("GRAND TOTAL: $" + booking.getGrandTotal() + " (Paid via " + paymentMethod + ")",
                boldFont));
        document.add(new Paragraph("\n--------------------------------------------------------------\n"));
        document.add(new Paragraph("Thank you for choosing Cinephile! Please present this ticket at the counter.",
                normalFont));

        document.close();
        return fileName;
    }
}
