package client;

import client.config.Config;
import client.utils.ServerUtils;
import commons.Note;


import javafx.collections.FXCollections;

import javafx.event.Event;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TagList {
    private final List<ComboBox<String>> filters;
    private List<String> tags;
    private final AnchorPane filterPane;
    private final ScrollBar scrollBar;
    private final double filterWidth = 100.0;
    private final double filterDistance = 5.0;

    private final DefaultCollectionService defaultCollectionService;
    private final ServerUtils serverUtils;
    private final DefaultCtrl defaultCtrl;
    private final Config config;

    private List<Note> filteredNotes;

    /**
     * Constructor for the TagList class.
     *
     * @param defaultCtrl               the controller for handling default actions
     * @param defaultCollectionService  the service for managing collections
     * @param serverUtils               utility class for server interactions
     * @param filterPane                the pane containing filters
     * @param scrollBar                 the scroll bar for the filter pane
     * @param clearButton               the button to clear all tags
     * @param config                    the config
     */
    public TagList(DefaultCtrl defaultCtrl,
                   DefaultCollectionService defaultCollectionService,
                   Config config,
                   ServerUtils serverUtils,
                   AnchorPane filterPane,
                   ScrollBar scrollBar,
                   Button clearButton) {
        this.defaultCtrl = defaultCtrl;
        this.defaultCollectionService = defaultCollectionService;
        this.serverUtils = serverUtils;
        this.scrollBar = scrollBar;
        this.filterPane = filterPane;
        this.tags = new ArrayList<>();
        this.filteredNotes = new ArrayList<>();
        this.config = config;

        filters = new ArrayList<>();

        clearButton.setOnMouseClicked(e -> clearTags());
        scrollBar.valueProperty().addListener((observable, oldValue, newValue) -> {
            filterPane.setTranslateX(-newValue.doubleValue());
        });

        scrollBar.setMin(0);
        scrollBar.setMax(0);
        filterPane.getStyleClass().add("filterPane");
    }

    /**
     * setter for filtered notes
     * @param notes notes
     */
    public void setNotes(List<Note> notes) {
        filteredNotes = notes;
    }

    /**
     * Sets the available tags by extracting them from notes.
     */
    public void setTags() {
        Pattern pattern = Pattern.compile("#(\\w+)");

        if(defaultCollectionService.getDefaultCollection().isPresent())
            defaultCollectionService.setDefaultCollection(
                    serverUtils.getNoteCollectionById(
                            defaultCollectionService.getDefaultCollection().get().getId()
                    )
            );

        tags = filteredNotes.stream()
                .map(Note::getContent)
                .map(x -> {
                    Matcher matcher = pattern.matcher(x);
                    List<String> ans = new ArrayList<>();
                    while (matcher.find()) {
                        ans.add(matcher.group(1));
                    }
                    return ans;
                })
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());

        tags.removeAll(filters.stream().map(ComboBox::getValue).toList());
        if (getLast() != null)
            getLast().setItems(FXCollections.observableList(tags));
    }

    /**
     * Retrieves the list of currently selected tags.
     *
     * @return a list of selected tags
     */
    public List<String> getSelectedTags() {
        return filters.stream()
                .map(ComboBox::getValue)
                .filter(Objects::nonNull)
                .filter(x -> !x.isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * Calculates the remaining width after placing filters.
     *
     * @return the remaining width
     */
    public double getWidthRemainder() {
        double x = 0;
        double w = filterPane.getWidth();
        while (x <= w)
            x += filterWidth + filterDistance;
        return x - w;
    }

    /**
     * Calculates the total horizontal length occupied by filters.
     *
     * @return the total length
     */
    public double getXLength() {
        double wd = 5;
        for (var tagBox : filters)
            wd += tagBox.getWidth();

        return wd + filters.size() * filterDistance;
    }

    /**
     * Gets the number of tag filters.
     *
     * @return the number of filters
     */
    public int getSize() {
        return filters.size();
    }

    /**
     * Adds a new tag filter (ComboBox) to the filter pane.
     */
    public void addTagBox() {
        for(ComboBox<String> tagBox : filters)
            if(tagBox.getSelectionModel().getSelectedItem() == null)
                return;

        ComboBox<String> tagBox = createTagBox();

        filterPane.getChildren().add(tagBox);

        AnchorPane.setLeftAnchor(tagBox, getXLength());

        filters.add(tagBox);
        AnchorPane.setTopAnchor(tagBox, 0.00);
        AnchorPane.setBottomAnchor(tagBox, 0.00);


        if (getXLength() + (filterWidth + filterDistance) > filterPane.getWidth()
                && filterPane.getWidth() > 0) {
            if (scrollBar.getMax() == 0) {
                scrollBar.setMax(getWidthRemainder());
                return;
            }

            System.out.println("Length Before: " + scrollBar.getMax());
            scrollBar.setMax(scrollBar.getMax() + (filterWidth + filterDistance));
            System.out.println("Length After: " + scrollBar.getMax());
        }
    }

    private boolean invalidTag(ComboBox<String> tagBox) {
        if(defaultCollectionService.getDefaultCollection().isEmpty()) {
            defaultCtrl.messageFromOtherScene(
                    "Filters only work within a collection",
                    true
            );
            tagBox.getItems().clear();
            return true;
        }

        return false;
    }

    /**
     * Creates a new tag filter (ComboBox).
     *
     * @return the created ComboBox
     */
    private ComboBox<String> createTagBox() {
        ComboBox<String> tagBox = new ComboBox<>();
        tagBox.setMaxWidth(filterWidth);
        tagBox.setMinWidth(filterWidth);
        tagBox.getStyleClass().add("comboBox");

        tagBox.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if(invalidTag(tagBox))
                event.consume();
        });

        tagBox.setOnAction(e -> {
            onAction(tagBox);
        });
        tagBox.setOnMouseClicked(e -> {
            System.out.println("updating tags..");
            setTags();
        });
        return tagBox;
    }

    /**
     * Selects a specific tag in the last filter.
     *
     * @param tag the tag to select
     */
    public void select(String tag) {
        if(invalidTag(filters.getLast()))
            return;

        getLast().setValue(tag);
    }

    /**
     * Handles the action when a tag is selected in a ComboBox.
     *
     * @param tagBox the ComboBox in which the action occurred
     */
    public void onAction(ComboBox<String> tagBox) {
        tagBox.setPromptText(tagBox.getSelectionModel().getSelectedItem());

        tagBox.setPrefWidth(Region.USE_COMPUTED_SIZE);
        System.out.println(tagBox.getWidth());

        tagBox.addEventFilter(javafx.scene.input.MouseEvent.ANY, Event::consume);
        tagBox.getStyleClass().clear();
        tagBox.getStyleClass().add("activatedTagBox");

        addTagBox();

//        defaultCtrl.messageFromOtherScene(
//                "Filter added",
//                false
//        );

        defaultCtrl.tagsChanged();
    }

    /**
     * Retrieves the value of a tag at a specific index.
     *
     * @param i the index of the tag
     * @return the value of the tag
     */
    public String getTagValue(int i) {
        return filters.get(i).getValue();
    }

    /**
     * Clears all tags and resets the filter pane.
     */
    public void clearTags() {
        filters.clear();
        filterPane.getChildren().clear();
        filterPane.translateXProperty().setValue(0);
        addTagBox();
        scrollBar.setMax(0);
        defaultCtrl.tagsChanged();
    }

    /**
     * Compares this TagList with another object for equality.
     *
     * @param o the object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TagList tagList = (TagList) o;
        return Objects.equals(filters, tagList.filters)
                && Objects.equals(filterPane, tagList.filterPane)
                && Objects.equals(scrollBar, tagList.scrollBar);
    }

    /**
     * Computes the hash code for this TagList.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(filters, filterPane, scrollBar, filterWidth, filterDistance);
    }

    /**
     * Retrieves the last tag filter (ComboBox) in the list.
     *
     * @return the last ComboBox or null if the list is empty
     */
    public ComboBox<String> getLast() {
        if (filters == null || filters.isEmpty())
            return null;
        return filters.getLast();
    }
}