package com.example.application.views.travelinfo;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import ai.peoplecode.OpenAIConversation;

import java.time.temporal.ChronoUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@PageTitle("")
@Menu(icon = "line-awesome/svg/user.svg", order = 0)
@Route("")
public class TravelInfoView extends Composite<VerticalLayout> {
    private OpenAIConversation conversation;
    private TextField travelersField;
    private ComboBox<SampleItem> petComboBox;
    private ComboBox<SampleItem> childrenComboBox;
    private TextField departureField;
    private TextField durationField;
    private TextField destinationField;
    private TextField monthField;
    private String generatedTripPlan;
    private String currentPlan;

    private Paragraph resultParagraph;
    private Button submitButton;

    private TextField askText;
    private Paragraph replyText;
    private Button askButton;

    private TextField followText;
    private Paragraph followReplyText;
    private Button followButton;

    private DatePicker datePicker;
    private DatePicker datePicker2;

    private TextField quotaText;
    private Button quotaButton;

    private TextField budgetText;
    private Button budgetButton;




    class MyClickListener implements ComponentEventListener<ClickEvent<Button>> {

        @Override
        public void onComponentEvent(ClickEvent<Button> event) {
            collectAndDisplayTravelInfo();
            String budget = budgetText.getValue();

            // Ask the conversation model for a detailed daily travel plan
            String reply = conversation.askQuestion(resultParagraph.getText(),
                    "generate a detailed daily travel plan for this trip. If users have to many days to travel, please find nearby places(within the destination country) into their plans. All days their have must be planned. All budget of " +
                            budget + " in departure place's currency must be spent. Please only present the daily total cost and daily plan. " +
                            "Present results for each day as day# or date, activities, any highlight, " +
                            "accommodation, transportation, total cost, and daily hints. Please write Total Trip Cost: in the end including payment for flight ticket.");

            // Convert the reply string into a StringBuilder for manipulation
            StringBuilder formattedReply = new StringBuilder(reply);
            generatedTripPlan=reply;
            currentPlan=generatedTripPlan;
            // Regular expression to match date format YYYY-MM-DD
            String datePattern = "\\d{4}-\\d{2}-\\d{2}";
            Pattern pattern = Pattern.compile(datePattern);
            Matcher matcher;

            // Loop through the reply to replace both "### Day #" and dates with <h3> tags, smaller size
            int index = 0;
            while (index < formattedReply.length()) {
                // Check for a date occurrence using regex (YYYY-MM-DD)
                matcher = pattern.matcher(formattedReply.substring(index));
                if (matcher.find() && matcher.start() == 0) {
                    // Replace date with <h3>DATE</h3>, using smaller text for the date
                    formattedReply.insert(index, "<h3 style='font-size: 18px;'>");
                    int endOfDateIndex = matcher.end() + index;
                    formattedReply.insert(endOfDateIndex, "</h3>");
                    index = endOfDateIndex + "</h3>".length();
                }
                // If no date is found, check for "### Day" occurrence
                else if (formattedReply.indexOf("### Day", index) == index) {
                    // Replace "###" with <h3> and insert </h3> at the end of the line, with smaller text
                    formattedReply.replace(index, index + 3, "<h3 style='font-size: 18px;'>");
                    int endOfLineIndex = formattedReply.indexOf("\n", index);
                    if (endOfLineIndex == -1) {
                        endOfLineIndex = formattedReply.length();
                    }
                    formattedReply.insert(endOfLineIndex, "</h3>");
                    index = endOfLineIndex + "</h3>".length();
                }
                // If "Total Trip Cost" is found, insert a new paragraph before it
                else if (formattedReply.indexOf("### Total Trip Cost", index) == index) {
                    formattedReply.replace(index, index + 3, "<p><strong>");
                    int endOfTotalCostIndex = formattedReply.indexOf("\n", index);
                    if (endOfTotalCostIndex == -1) {
                        endOfTotalCostIndex = formattedReply.length();
                    }
                    formattedReply.insert(endOfTotalCostIndex, "</strong></p>");
                    index = endOfTotalCostIndex + "</p>".length();
                }
                else {
                    index++;
                }
            }

            // Handle Markdown-style bold text (**Activities:** to <strong>Activities:</strong>)
            int boldStart;
            while ((boldStart = formattedReply.indexOf("**")) != -1) {
                formattedReply.replace(boldStart, boldStart + 2, "<strong>");
                int boldEnd = formattedReply.indexOf("**", boldStart + 7); // After <strong>
                if (boldEnd != -1) {
                    formattedReply.replace(boldEnd, boldEnd + 2, "</strong>");
                }
            }

            // Set the formatted reply with HTML tags to display the content properly
            replyText.getElement().setProperty("innerHTML", formattedReply.toString());
            //save the reply for use in the follow-up listener//



            // Clear the input field
            askText.clear();
        }
    }
    class QuoteClickListener
            implements ComponentEventListener<ClickEvent<Button>> {

        @Override
        public void onComponentEvent(ClickEvent<Button> event) {
            collectAndDisplayTravelInfo();
            String reply= conversation.askQuestion(resultParagraph.getText(), "Estimate the total cost range for this trip.  please present only number range without other description. the range shows in users' currency and exchange to local currency. please only present digit number range and both currencies signs. eg. 1 usd/7CNY");
            quotaText.setValue(reply);
            //quotaText.clear();
        }
    }



    class FollowUpClickListener implements ComponentEventListener<ClickEvent<Button>> {

        @Override
        public void onComponentEvent(ClickEvent<Button> event) {
            collectAndDisplayTravelInfo();
            String newFollowUpQuestion = followText.getValue();
            //update the followUpQuestion//
            String followUpQuestion=newFollowUpQuestion;
            //use the currentPlan(which might include generateTripPlan) as context for followup//
            String contentForFollowUp = currentPlan+resultParagraph.getText();

            // Get the follow-up reply from the conversation model
            String followUpReply = conversation.askQuestion(contentForFollowUp, followUpQuestion+ budgetText.getValue());
            //update the current plan//
            currentPlan= followUpReply;
            // Convert the follow-up reply string into a StringBuilder for manipulation//
            StringBuilder formattedFollowUpReply = new StringBuilder(followUpReply);



            // Regular expression to match date format YYYY-MM-DD
            String datePattern = "\\d{4}-\\d{2}-\\d{2}";
            Pattern pattern = Pattern.compile(datePattern);
            Matcher matcher;

            // Loop through the follow-up reply to replace both "### Day #" and dates with <h3> tags, smaller size
            int index = 0;
            while (index < formattedFollowUpReply.length()) {
                // Check for a date occurrence using regex (YYYY-MM-DD)
                matcher = pattern.matcher(formattedFollowUpReply.substring(index));
                if (matcher.find() && matcher.start() == 0) {
                    // Replace date with <h3>DATE</h3>, using smaller text for the date
                    formattedFollowUpReply.insert(index, "<h3 style='font-size: 18px;'>");
                    int endOfDateIndex = matcher.end() + index;
                    formattedFollowUpReply.insert(endOfDateIndex, "</h3>");
                    index = endOfDateIndex + "</h3>".length();
                }
                // If no date is found, check for "### Day" occurrence
                else if (formattedFollowUpReply.indexOf("### Day", index) == index) {
                    // Replace "###" with <h3> and insert </h3> at the end of the line, with smaller text
                    formattedFollowUpReply.replace(index, index + 3, "<h3 style='font-size: 18px;'>");
                    int endOfLineIndex = formattedFollowUpReply.indexOf("\n", index);
                    if (endOfLineIndex == -1) {
                        endOfLineIndex = formattedFollowUpReply.length();
                    }
                    formattedFollowUpReply.insert(endOfLineIndex, "</h3>");
                    index = endOfLineIndex + "</h3>".length();
                }
                // If "Total Trip Cost" is found, insert a new paragraph before it
                else if (formattedFollowUpReply.indexOf("### Total Trip Cost", index) == index) {
                    formattedFollowUpReply.replace(index, index + 3, "<p><strong>");
                    int endOfTotalCostIndex = formattedFollowUpReply.indexOf("\n", index);
                    if (endOfTotalCostIndex == -1) {
                        endOfTotalCostIndex = formattedFollowUpReply.length();
                    }
                    formattedFollowUpReply.insert(endOfTotalCostIndex, "</strong></p>");
                    index = endOfTotalCostIndex + "</p>".length();
                }
                else {
                    index++;
                }
            }

            // Handle Markdown-style bold text (**Activities:** to <strong>Activities:</strong>)
            int boldStart;
            while ((boldStart = formattedFollowUpReply.indexOf("**")) != -1) {
                formattedFollowUpReply.replace(boldStart, boldStart + 2, "<strong>");
                int boldEnd = formattedFollowUpReply.indexOf("**", boldStart + 7); // After <strong>
                if (boldEnd != -1) {
                    formattedFollowUpReply.replace(boldEnd, boldEnd + 2, "</strong>");
                }
            }

            // Set the formatted follow-up reply with HTML tags to display the content properly
            followReplyText.getElement().setProperty("innerHTML", formattedFollowUpReply.toString());



            // Clear the input field
            followText.clear();
        }
    }

    public String getOpenAIKey() {
        String apiKey = System.getenv("OPENAI_API_KEY");
        if (apiKey == null)
        { throw new IllegalStateException("API Key not found in environment variables");
        }
        return apiKey;
    }
    // adjust the sizes of reply box and followup box based on trip duration//
    private void adjustTextAreasHeight(){
        LocalDate startDate=datePicker.getValue();
        LocalDate endDate=datePicker2.getValue();
        String durationInput=durationField.getValue();
        int rowHeight=35;
        int rowsPerday=4;
        int minH=100;
        if(startDate!=null&&endDate!=null){
            long durationDay=ChronoUnit.DAYS.between(startDate,endDate)-1;
            if(durationDay<3)
            {durationDay=3;}
            else if(durationDay>=10&&durationDay<=15)
            {durationDay=durationDay-3;}
            else if(durationDay>=16&&durationDay<=30)
            {durationDay=durationDay-5;}
            else if(durationDay>30){
                durationDay=durationDay-8;
            }
            int height=(int)Math.max(minH,durationDay*rowsPerday*rowHeight);
            replyText.setHeight(height+2+"px");
            followReplyText.setHeight(height+2+"px");
        }else if(!durationInput.isEmpty()){
            try{
                int durationDay=Integer.parseInt(durationInput);
                if(durationDay<3)
                {durationDay=3;}
                else if(durationDay>=10&&durationDay<=15)
                {durationDay=durationDay-3;}
                else if(durationDay>=16&&durationDay<=30)
                {durationDay=durationDay-5;}
                else if(durationDay>30){
                    durationDay=durationDay-8;
                }
                int height=(int)Math.max(minH,durationDay*rowHeight*rowsPerday);
                replyText.setHeight(height+2+"px");
                followReplyText.setHeight(height+2+"px");
            }catch (NumberFormatException e){
                replyText.setHeight("100px");
                followReplyText.setHeight("100px");
            }
        }
    }

    public TravelInfoView() {
        conversation = new OpenAIConversation(getOpenAIKey(), "gpt-4o-mini");

        askText = new TextField();
        askText.setLabel("Help me plan this travel");
        askText.setWidth("400px");

        askButton = new Button();
        askButton.setText("Plan the trip");
        askButton.setWidth("min-content");
        askButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        replyText = new Paragraph();
        replyText.setWidth("100%");

        replyText.getStyle().remove("border");

        followText = new TextField();
        followText.setLabel("If you have additional needs or requirements, please type below");
        followText.setWidth("100%");

        followButton = new Button();
        followButton.setText("Follow-up");
        followButton.setWidth("min-content");
        followButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);



        followReplyText = new Paragraph();
        followReplyText.setWidth("100%");
        followReplyText.getStyle().remove("border");


        quotaText = new TextField();
        //quotaText.setLabel("you can get your budget");
        quotaText.setWidth("100%");

        quotaButton = new Button();
        quotaButton.setText("Get a quote");
        quotaButton.setWidth("min-content");
        quotaButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        budgetText = new TextField();
        budgetText.setLabel("Please give me your budget in your currency");
        budgetText.setWidth("100%");

        budgetButton = new Button();
        budgetButton.setText("Budget");
        budgetButton.setWidth("min-content");
        budgetButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        travelersField = new TextField();
        petComboBox = new ComboBox<>();
        childrenComboBox = new ComboBox<>();
        departureField = new TextField();
        durationField = new TextField();
        destinationField = new TextField();
        monthField = new TextField();
        resultParagraph = new Paragraph();
        datePicker = new DatePicker();
        datePicker2 = new DatePicker();


        // Set labels and width for each field
        travelersField.setLabel("Number Of Travelers");
        travelersField.setWidth("100%");

        petComboBox.setLabel("Number Of Pets");
        petComboBox.setWidth("100%");
        setComboBoxSampleData(petComboBox);

        childrenComboBox.setLabel("Number Of Children");
        childrenComboBox.setWidth("100%");
        setComboBoxSampleData(childrenComboBox);

        departureField.setLabel("Departure From");
        departureField.setWidth("100%");

        durationField.setLabel("Duration (If you have exactly date, you can leave it empty.)");
        durationField.setWidth("100%");

        destinationField.setLabel("Destination");
        destinationField.setWidth("100%");

        monthField.setLabel("Which month do you want to travel?(If you have exactly date, you can leave it empty.)");
        monthField.setWidth("100%");

        datePicker.setLabel("Start Date(If you don't have exactly date, you can leave it empty.)");
        datePicker.setMin(LocalDate.now());
        datePicker.setWidth("100%");

        datePicker2.setLabel("End Date(If you don't have exactly date, you can leave it empty.)");
        datePicker2.setWidth("100%");

        // Button to collect the data and display the result
        submitButton = new Button("Confirm");
        submitButton.setWidth("min-content");
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        submitButton.addClickListener(click ->{collectAndDisplayTravelInfo();});
        durationField.addValueChangeListener(event ->adjustTextAreasHeight() );
        datePicker.addValueChangeListener(event ->adjustTextAreasHeight() );
        datePicker2.addValueChangeListener(event ->adjustTextAreasHeight() );
        //let ending date later than starting date//
        datePicker.addValueChangeListener(event -> {
            LocalDate selectedStartDate = event.getValue();
            if (selectedStartDate != null) {
                datePicker2.setMin(selectedStartDate.plusDays(1));
            } else {
                datePicker2.setMin(null);
            }
        });


        // Apply the same styles as in HomeView's replyText
        resultParagraph.setWidth("80%");
        resultParagraph.setHeight("100px");
        resultParagraph.getStyle().set("border", "1px solid black");
        //resultParagraph.getStyle().set("padding", "15px");
        //resultParagraph.getStyle().set("margin-top", "20px");

        HorizontalLayout askButtonLayout = new HorizontalLayout(askButton);
        askButtonLayout.setWidthFull(); // Set the layout to full width
        askButtonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.START); // Align buttons to the left

        // For the follow-up button, put it in a separate layout if needed
        HorizontalLayout followButtonLayout = new HorizontalLayout(followButton);
        followButtonLayout.setWidthFull(); // Set the layout to full width
        followButtonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.START); // Align button to the left

        HorizontalLayout budgetButtonLayout = new HorizontalLayout(budgetButton);
        budgetButtonLayout.setWidthFull(); // Set the layout to full width
        budgetButtonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.START);

        HorizontalLayout quotaButtonLayout = new HorizontalLayout(quotaButton);
        quotaButtonLayout.setWidthFull(); // Set the layout to full width
        quotaButtonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.START);

        // Layout configuration
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        getContent().setJustifyContentMode(JustifyContentMode.START);
        getContent().setAlignItems(Alignment.CENTER);

        // Add components to layout
        getContent().add(travelersField, petComboBox, childrenComboBox, departureField, destinationField,datePicker,datePicker2,monthField,durationField, quotaButtonLayout,quotaText,budgetText,askButtonLayout,replyText,followText,followButtonLayout,followReplyText);
        askButton.addClickListener(new MyClickListener());
        followButton.addClickListener(new FollowUpClickListener());
        quotaButton.addClickListener(new QuoteClickListener());
    }

    record SampleItem(String value, String label, Boolean disabled) {
    }

    private void setComboBoxSampleData(ComboBox<SampleItem> comboBox) {
        List<SampleItem> sampleItems = new ArrayList<>();
        sampleItems.add(new SampleItem("Zero", "Zero", null));
        sampleItems.add(new SampleItem("One", "One", null));
        sampleItems.add(new SampleItem("Two", "Two", Boolean.TRUE));
        sampleItems.add(new SampleItem("Three", "Three", null));
        comboBox.setItems(sampleItems);
        comboBox.setItemLabelGenerator(SampleItem::label);
    }
    //get date picker schedule//
    private List<Integer> getDaysOfMonth(Month month, int year) {
        List<Integer> days = new ArrayList<>();
        int lengthOfMonth = month.length(LocalDate.of(year, month, 1).isLeapYear());

        for (int day = 1; day <= lengthOfMonth; day++) {
            days.add(day);
        }

        return days;
    }
    private void collectAndDisplayTravelInfo(){
        String travelers = travelersField.getValue();
        String pet = petComboBox.getValue() != null ? petComboBox.getValue().label() : "No Pet";
        String children = childrenComboBox.getValue() != null ? childrenComboBox.getValue().label() : "No Children";
        String departure = departureField.getValue();
        String duration = durationField.getValue();
        String destination = destinationField.getValue();
        String month = monthField.getValue();

        // Get the date values from the DatePickers
        LocalDate startDate = datePicker.getValue();
        LocalDate endDate = datePicker2.getValue();

        // Format the dates, if they are not null
        String startDateString = startDate != null ? startDate.toString() : "Not selected";
        String endDateString = endDate != null ? endDate.toString() : "Not selected";

        // Create a paragraph with all the collected information, including dates
        String resultText = String.format(
                "Travelers: %s, Pet: %s, Children: %s, Departure: %s, Start Date: %s, End Date: %s, Duration: %s days, Destination: %s, Months: %s,",
                travelers, pet, children, departure, startDateString, endDateString, duration, destination,month
        );

        // Display the result in the paragraph
        resultParagraph.setText(resultText);
    }




}
