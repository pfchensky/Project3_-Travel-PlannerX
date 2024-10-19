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



    class MyClickListener
            implements ComponentEventListener<ClickEvent<Button>> {

        @Override
        public void onComponentEvent(ClickEvent<Button> event) {
            collectAndDisplayTravelInfo();
            String reply= conversation.askQuestion(resultParagraph.getText()+"plan with new budget"+budgetText.getValue(), "help me plan this trip. ");
            replyText.setText(reply);
            askText.clear();
        }
    }

    class QuotaClickListener
            implements ComponentEventListener<ClickEvent<Button>> {

        @Override
        public void onComponentEvent(ClickEvent<Button> event) {
            collectAndDisplayTravelInfo();
            String reply= conversation.askQuestion(resultParagraph.getText(), "give me a range for total cost value number. ");
            quotaText.setValue(reply);
            //quotaText.clear();
        }
    }

    class FollowUpClickListener
            implements ComponentEventListener<ClickEvent<Button>> {

        @Override
        public void onComponentEvent(ClickEvent<Button> event) {
            collectAndDisplayTravelInfo();
            String followUpQuestion = followText.getValue()+budgetText.getValue();
            String currentPlan = resultParagraph.getText();
            String followUpReply= conversation.askQuestion(currentPlan, followUpQuestion);
            followReplyText.setText(followUpReply);
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

    public TravelInfoView() {
        conversation = new OpenAIConversation(getOpenAIKey(), "gpt-4o-mini");

        askText = new TextField();
        askText.setLabel("Help me plan this travel");
        askText.setWidth("400px");

        askButton = new Button();
        askButton.setText("Generate the plan");
        askButton.setWidth("min-content");
        askButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        replyText = new Paragraph();
        replyText.setWidth("100%");
        replyText.setHeight("600px");
        replyText.getStyle().set("border", "1px solid black");

        followText = new TextField();
        followText.setLabel("If you have additional needs or requirements, please type below");
        followText.setWidth("100%");

        followButton = new Button();
        followButton.setText("Follow-up");
        followButton.setWidth("min-content");
        followButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);



        followReplyText = new Paragraph();
        followReplyText.setWidth("100%");
        followReplyText.setHeight("600px");
        followReplyText.getStyle().set("border", "1px solid black");


        quotaText = new TextField();
        //quotaText.setLabel("you can get your budget");
        quotaText.setWidth("100%");

        quotaButton = new Button();
        quotaButton.setText("Get a quota");
        quotaButton.setWidth("min-content");
        quotaButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        budgetText = new TextField();
        budgetText.setLabel("Please give me your budget");
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
        datePicker.setWidth("100%");

        datePicker2.setLabel("End Date(If you don't have exactly date, you can leave it empty.)");
        datePicker2.setWidth("100%");

        // Button to collect the data and display the result
        submitButton = new Button("Confirm");
        submitButton.setWidth("min-content");
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        submitButton.addClickListener(click ->{collectAndDisplayTravelInfo();});


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
        quotaButton.addClickListener(new QuotaClickListener());
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
