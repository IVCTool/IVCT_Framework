package nato.ivct.gui.client.sut;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

import org.eclipse.scout.rt.client.dto.FormData;
import org.eclipse.scout.rt.client.ui.action.menu.AbstractMenu;
import org.eclipse.scout.rt.client.ui.action.menu.IMenuType;
import org.eclipse.scout.rt.client.ui.action.menu.TableMenuType;
import org.eclipse.scout.rt.client.ui.basic.filechooser.FileChooser;
import org.eclipse.scout.rt.client.ui.basic.table.AbstractTable;
import org.eclipse.scout.rt.client.ui.basic.table.ColumnSet;
import org.eclipse.scout.rt.client.ui.basic.table.ITableRow;
import org.eclipse.scout.rt.client.ui.basic.table.TableRow;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractLongColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractStringColumn;
import org.eclipse.scout.rt.client.ui.desktop.OpenUriAction;
import org.eclipse.scout.rt.client.ui.form.AbstractForm;
import org.eclipse.scout.rt.client.ui.form.AbstractFormHandler;
import org.eclipse.scout.rt.client.ui.form.IForm;
import org.eclipse.scout.rt.client.ui.form.fields.GridData;
import org.eclipse.scout.rt.client.ui.form.fields.accordionfield.AbstractAccordionField;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.splitbox.AbstractSplitBox;
import org.eclipse.scout.rt.client.ui.form.fields.tablefield.AbstractTableField;
import org.eclipse.scout.rt.client.ui.group.AbstractGroup;
import org.eclipse.scout.rt.client.ui.group.IGroup;
import org.eclipse.scout.rt.client.ui.tile.AbstractHtmlTile;
import org.eclipse.scout.rt.client.ui.tile.AbstractTileAccordion;
import org.eclipse.scout.rt.client.ui.tile.AbstractTileGrid;
import org.eclipse.scout.rt.client.ui.tile.ITile;
import org.eclipse.scout.rt.client.ui.tile.TileGridLayoutConfig;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.exception.ProcessingException;
import org.eclipse.scout.rt.platform.resource.BinaryResource;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.platform.util.CollectionUtility;
import org.eclipse.scout.rt.platform.util.TriState;
import org.eclipse.scout.rt.shared.data.tile.TileColorScheme;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.LoggerFactory;

import com.cedarsoftware.util.io.JsonWriter;

import nato.ivct.gui.client.OptionsForm.MainBox.OkButton;
import nato.ivct.gui.client.sut.SuTCbForm.MainBox.MainBoxHorizontalSplitBox.AccordionField;
import nato.ivct.gui.client.sut.SuTCbForm.MainBox.MainBoxHorizontalSplitBox.AccordionField.Accordion;
import nato.ivct.gui.client.sut.SuTCbForm.MainBox.MainBoxHorizontalSplitBox.AccordionField.Accordion.TileGroup;
import nato.ivct.gui.client.sut.SuTCbForm.MainBox.MainBoxHorizontalSplitBox.SutParameterBox.ParameterHorizontalSplitterBox.SutTcExtraParameterTableField;
import nato.ivct.gui.client.sut.SuTCbForm.MainBox.MainBoxHorizontalSplitBox.SutParameterBox.ParameterHorizontalSplitterBox.SutTcParameterTableField;
import nato.ivct.gui.client.sut.SuTCbForm.MainBox.MainBoxHorizontalSplitBox.SutParameterBox.ParameterHorizontalSplitterBox.SutTcParameterTableField.SuTTcParameterTable.SaveMenu;
import nato.ivct.gui.shared.cb.ICbService;
import nato.ivct.gui.shared.cb.ITsService;
import nato.ivct.gui.shared.sut.ISuTCbService;
import nato.ivct.gui.shared.sut.SuTCbFormData;


@FormData(value = SuTCbFormData.class, sdkCommand = FormData.SdkCommand.CREATE)
public class SuTCbForm extends AbstractForm {
    // content part of a TC tile content
    private static final String  TC_NAME_KW                   = "tcName";
    private static final String  TC_VERDICT_KW                = "tcVerdict";
    private static final Pattern TILE_TITLE_CONTENT_PART      = Pattern.compile("(<div.*?=)(.*?)(>.*)");
    private static final Pattern TC_NAME_TILE_CONTENT_PART    = Pattern.compile("(<div.*?<p.*?name=.*?" + TC_NAME_KW + ".*?>)(.*?)(</p>.*)");
    private static final Pattern TC_VERDICT_TILE_CONTENT_PART = Pattern.compile("(<div.*?<p.*?name=.*?" + TC_VERDICT_KW + ".*?>)(.*?)(</p>.*)");

    // verdicts
    public static final String PASSED_VERDICT       = "PASSED";
    public static final String INCONCLUSIVE_VERDICT = "INCONCLUSIVE";
    public static final String FAILED_VERDICT       = "FAILED";
    public static final String NOT_RUN_VERDICT      = "NOT_RUN";

    private String sutId      = null;
    private String cbId       = null;
    private String activeTsId = null;

    org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

    // unique table row ID generator
    private final AtomicLong m_nextRowId = new AtomicLong();


    @Override
    protected String getConfiguredTitle() {
        return TEXTS.get("Badge");
    }


    @Override
    protected int getConfiguredDisplayHint() {
        return IForm.DISPLAY_HINT_VIEW;
    }


    @Override
    protected String getConfiguredDisplayViewId() {
        return IForm.VIEW_ID_CENTER;
    }


    @Override
    public Object computeExclusiveKey() {
        return getCbId();
    }


    public void startView() throws ProcessingException {
        startInternal(new ViewHandler());
    }


    public AccordionField getAccordionField() {
        return getFieldByClass(AccordionField.class);
    }


    public SutTcParameterTableField getSutTcParameterTableField() {
        return getFieldByClass(SutTcParameterTableField.class);
    }


    public SutTcExtraParameterTableField getSutExtraParameterTableField() {
        return getFieldByClass(SutTcExtraParameterTableField.class);
    }


    public MainBox getMainBox() {
        return getFieldByClass(MainBox.class);
    }


    public OkButton getOkButton() {
        return getFieldByClass(OkButton.class);
    }


    @FormData
    public String getCbId() {
        return cbId;
    }


    @FormData
    public void setCbId(final String _cbId) {
        cbId = _cbId;
    }


    @FormData
    public String getSutId() {
        return sutId;
    }


    @FormData
    public void setSutId(final String _sutId) {
        sutId = _sutId;
    }


    @FormData
    public String getActiveTsId() {
        return activeTsId;
    }


    @FormData
    public void setActiveTsId(String _activeTsId) {
        activeTsId = _activeTsId;
    }

    @Order(1000)
    public class MainBox extends AbstractGroupBox {

        @Override
        protected int getConfiguredGridColumnCount() {
            return 5;
        }


        // main box shall not be scrollable to keep it in its size
        @Override
        protected TriState getConfiguredScrollable() {
            return TriState.FALSE;
        }

        @Order(1000)
        public class MainBoxHorizontalSplitBox extends AbstractSplitBox {
            @Override
            protected boolean getConfiguredSplitHorizontal() {
                // split horizontal
                return false;
            }


            @Override
            protected double getConfiguredSplitterPosition() {
                return 0.25;
            }

            @Order(1000)
            public class AccordionField extends AbstractAccordionField<AccordionField.Accordion> {

                @Override
                protected String getConfiguredLabel() {
                    return TEXTS.get("Testsuites");
                }


                @Override
                protected int getConfiguredGridW() {
                    return FULL_WIDTH;
                }

                public class Accordion extends AbstractTileAccordion<ITile> {

                    @Override
                    protected boolean getConfiguredExclusiveExpand() {
                        return true;
                    }


                    @Override
                    protected void handleGroupCollapsedChange(IGroup group) {
                        final String groupTitle = group.getTitle();

                        if (isInitDone()) {
                            if (group.isCollapsed()) {
                                if (groupTitle.equals(getActiveTsId())) {
                                    // clear parameter tables
                                    getSutTcParameterTableField().clearTcParamTable();
                                    getSutExtraParameterTableField().clearTcExtraParamTable();
                                    setActiveTsId(null);
                                }
                            }
                            else {
                                // load TC parameters
                                setActiveTsId(groupTitle);
                                getSutTcParameterTableField().loadTcParamTable(groupTitle);
                                getSutExtraParameterTableField().loadTcExtraParamTable(groupTitle);
                            }

                            // hide the save menu for the tc param table
                            getSutTcParameterTableField().getTable().getMenuByClass(SaveMenu.class).setVisible(false);
                        }
                        super.handleGroupCollapsedChange(group);
                    }

                    public class TileGroup extends AbstractGroup {

                        @Override
                        public TileGrid getBody() {
                            return (TileGrid) super.getBody();
                        }

                        public class TileGrid extends AbstractTileGrid<ITile> {
                            @Override
                            protected boolean getConfiguredSelectable() {
                                return true;
                            }


                            // only 1 tile/TC at a time is selectable
                            @Override
                            protected boolean getConfiguredMultiSelect() {
                                return false;
                            }


                            @Override
                            protected int getConfiguredGridColumnCount() {
                                return 6;
                            }


                            @Override
                            protected TileGridLayoutConfig getConfiguredLayoutConfig() {
                                return super.getConfiguredLayoutConfig().withColumnWidth(100).withRowHeight(100);
                            }


                            @Override
                            protected boolean getConfiguredScrollable() {
                                return false;
                            }
                        }
                    }
                }

            }

            @Order(2000)
            public class SutParameterBox extends AbstractGroupBox {
                @Order(1000)
                public class ParameterHorizontalSplitterBox extends AbstractSplitBox {
                    @Override
                    protected boolean getConfiguredSplitHorizontal() {
                        // split horizontal
                        return false;
                    }


                    @Override
                    protected double getConfiguredSplitterPosition() {
                        return 0.6;
                    }

                    @Order(1000)
                    public class SutTcParameterTableField extends AbstractTableField<SutTcParameterTableField.SuTTcParameterTable> {
                        List<ITableRow> mRows = new ArrayList<>();


                        @Override
                        protected int getConfiguredGridH() {
                            return 3;
                        }


                        @Override
                        protected int getConfiguredGridW() {
                            return 3;
                        }


                        @Override
                        protected String getConfiguredLabel() {
                            return TEXTS.get("TcParameterSettings");
                        }


                        @Override
                        protected void execReloadTableData() {
                            getTable().discardAllRows();
                            getTable().addRows(mRows);
                            getTable().expandAll(null);

                            // clear modified status and hide save menu
                            getSutTcParameterTableField().execMarkSaved();
                            getSutTcParameterTableField().getTable().getMenuByClass(SaveMenu.class).setVisible(false);
                        }


                        // Save the TC parameters from the SuTCbParameterTable
                        @Override
                        public void doSave() {
                            final SuTTcParameterTable tbl = getSutTcParameterTableField().getTable();
                            final List<ITableRow> rows = tbl.getRows();

                            // map to record all JSONObject and all JSONArray
                            final HashMap<ITableRow, Object> jsonElements = new HashMap<>();

                            // root JSON object
                            final JSONObject jsonObjects = new JSONObject();
                            // record root JSON object
                            jsonElements.put(null, jsonObjects);

                            // interate over all children and grand-children of the root object
                            for (final ITableRow row: rows) {
                                final ITableRow parentRow = row.getParentRow();

                                final String key = row.getCell(tbl.getParameterNameColumn()).getText();
                                final String value = row.getCell(tbl.getParameterValueColumn()).getText();

                                // if the parent object is already known, then use it. Otherwise create it
                                final Object jsonParentElement = jsonElements.getOrDefault(parentRow, jsonObjects);
                                Object jsonElement = null;

                                if ("[".equals(value)) {
                                    jsonElement = new JSONArray();
                                    jsonElements.put(row, jsonElement);
                                }
                                else if ("{".equals(value)) {
                                    jsonElement = new JSONObject();
                                    jsonElements.put(row, jsonElement);
                                }
                                else {
                                    if (jsonParentElement instanceof JSONArray && key != null) {
                                        jsonElement = new JSONObject();
                                        ((JSONObject) jsonElement).put(key, value);
                                    }
                                    else
                                        jsonElement = value;
                                }

                                if (jsonParentElement instanceof JSONArray)
                                    ((JSONArray) jsonParentElement).add(jsonElement);
                                else
                                    ((JSONObject) jsonParentElement).put(key != null ? key : "", jsonElement);
                            }

                            final ISuTCbService service = BEANS.get(ISuTCbService.class);
                            final Map<String, Object> options = new HashMap<>();
                            options.put(JsonWriter.PRETTY_PRINT, true);
                            options.put(JsonWriter.TYPE, false);
                            service.storeTcParams(getSutId(), getCbId(), JsonWriter.objectToJson(jsonObjects, options));

                            // call super
                            super.doSave();
                        }


                        protected void loadTcParamTable(String tsId) {
                            clearTcParamTable();
                            getTable().addRows(loadAllParamRows());
                            getTable().expandAll(null);
                            getSutTcParameterTableField().execMarkSaved();
                        }


                        protected void clearTcParamTable() {
                            mRows.clear();
                            getTable().deleteAllRows();
                            getSutTcParameterTableField().execMarkSaved();
                        }


                        private List<ITableRow> loadAllParamRows() {
                            final ISuTCbService service = BEANS.get(ISuTCbService.class);
                            final String sParams = service.loadTcParams(getSutId(), getActiveTsId());
                            if (sParams != null) {
                                // param file does not exist
                                final JSONParser parser = new JSONParser();
                                Object jParams = null;
                                try {
                                    jParams = parser.parse(sParams);
                                }
                                catch (final ParseException e) {
                                    e.printStackTrace();
                                }

                                if (jParams != null) {
                                    addJsonObjectToTable(null, jParams);
                                }
                            }

                            return mRows;
                        }


                        private void addJsonObjectToTable(final ITableRow parentRow, final Object jObject) {
                            if (jObject instanceof JSONObject) {
                                if (((JSONObject) jObject).isEmpty())
                                    return;

                                // handle JSONObject of the form (key:value)
                                ((JSONObject) jObject).forEach((key, value) -> {
                                    if (value instanceof JSONObject || value instanceof JSONArray) {
                                        // value is itself a JSONObject or JSONArray
                                        final ITableRow newRow = addElementToTable(parentRow, key.toString(), value instanceof JSONObject ? "{" : "[");
                                        addJsonObjectToTable(newRow, value);
                                    }
                                    else {
                                        // value is a simple object
                                        addElementToTable(parentRow, key.toString(), Objects.toString(value, null));
                                    }
                                });
                            }
                            else if (jObject instanceof JSONArray) {
                                // handle as JSONArray
                                ((JSONArray) jObject).forEach(value -> addJsonObjectToTable(parentRow, value));
                            }
                            else {
                                // handle as element without having a key
                                addElementToTable(parentRow, null, jObject.toString());
                            }
                        }


                        private ITableRow addElementToTable(final ITableRow parentRow, final String key, final String value) {
                            final SuTTcParameterTable table = getTable();
                            final ITableRow row = table.createRow();
                            table.getIdColumn().setValue(row, m_nextRowId.getAndIncrement());
                            table.getParentIdColumn().setValue(row, Optional.ofNullable(parentRow).map(r -> table.getIdColumn().getValue(parentRow)).orElse(null));
                            table.getParameterNameColumn().setValue(row, key);
                            table.getParameterValueColumn().setValue(row, value);

                            // add row to table
                            mRows.add(row);

                            return row;
                        }


                        private void newRowWithParent(final ITableRow parent) {
                            final SuTTcParameterTable table = getTable();
                            final ColumnSet cols = table.getColumnSet();
                            final ITableRow row = new TableRow(cols);

                            row.getCellForUpdate(table.getIdColumn()).setValue(m_nextRowId.getAndIncrement());
                            row.getCellForUpdate(table.getParentIdColumn()).setValue(Optional.ofNullable(table.getIdColumn().getValue(parent)).orElse(null));

                            table.addRow(row, true);
                        }

                        public class SuTTcParameterTable extends AbstractTable {

                            public ParentIdColumn getParentIdColumn() {
                                return getColumnSet().getColumnByClass(ParentIdColumn.class);
                            }


                            public IdColumn getIdColumn() {
                                return getColumnSet().getColumnByClass(IdColumn.class);
                            }


                            public ParameterNameColumn getParameterNameColumn() {
                                return getColumnSet().getColumnByClass(ParameterNameColumn.class);
                            }


                            public ParameterValueColumn getParameterValueColumn() {
                                return getColumnSet().getColumnByClass(ParameterValueColumn.class);
                            }


                            @Override
                            protected void execDecorateRow(ITableRow row) {
                                if (getParameterValueColumn().getValue(row) == null) {
                                    row.setIconId("font:\uE001");
                                }
                            }


                            @Override
                            protected void execContentChanged() {
                                // show save menu if a table value was changed
                                if (execIsSaveNeeded()) {
                                    getSutTcParameterTableField().getTable().getMenuByClass(SaveMenu.class).setVisible(true);
                                }
                            }

                            @Order(10)
                            public class IdColumn extends AbstractLongColumn {

                                @Override
                                protected boolean getConfiguredDisplayable() {
                                    return false;
                                }


                                @Override
                                protected boolean getConfiguredPrimaryKey() {
                                    return true;
                                }

                            }

                            @Order(15)
                            public class ParentIdColumn extends AbstractLongColumn {

                                @Override
                                protected boolean getConfiguredParentKey() {
                                    return true;
                                }


                                @Override
                                protected boolean getConfiguredDisplayable() {
                                    return false;
                                }
                            }

                            @Order(1000)
                            public class ParameterNameColumn extends AbstractStringColumn {

                                @Override
                                protected String getConfiguredHeaderText() {
                                    return TEXTS.get("ParameterName");
                                }


                                @Override
                                protected int getConfiguredWidth() {
                                    return 200;
                                }
                            }

                            @Order(2000)
                            public class ParameterValueColumn extends AbstractStringColumn {
                                @Override
                                protected String getConfiguredHeaderText() {
                                    return TEXTS.get("ParameterValue");
                                }


                                @Override
                                protected int getConfiguredWidth() {
                                    return 200;
                                }
                            }

                            @Order(3000)
                            public class EditMenu extends AbstractMenu {

                                @Override
                                protected Set<? extends IMenuType> getConfiguredMenuTypes() {
                                    return CollectionUtility.<IMenuType> hashSet(TableMenuType.EmptySpace);
                                }


                                @Override
                                protected String getConfiguredText() {
                                    return TEXTS.get("Edit");
                                }


                                @Override
                                protected void execAction() {
                                    final SuTTcParameterTable tbl = getSutTcParameterTableField().getTable();
                                    tbl.getParameterValueColumn().setEditable(true);
                                    tbl.getMenuByClass(EditMenu.class).setEnabled(false);

                                    // enable for use of adding/deleting parameters
                                    // N.B.: excluded from usage for the moment until functionality is completely
                                    // implemented
                                    if (false) {
                                        tbl.getMenuByClass(NewMenu.class).setVisible(true);
                                        tbl.getMenuByClass(DeleteMenu.class).setVisible(true);
                                    }
                                    else {
                                        tbl.getRows().forEach(row -> {
                                            if (row.getCellValue(tbl.getParameterValueColumn().getColumnIndex()).toString().equals("[") || row.getCellValue(tbl.getParameterValueColumn().getColumnIndex()).toString().equals("{")) {
                                                row.setEnabled(false);
                                                tbl.getMenuByClass(AbortMenu.class).setVisible(true);
                                                row.getCell(getParameterNameColumn());
                                            }
                                        });
                                    }

                                    tbl.getMenuByClass(AbortMenu.class).setVisible(true);
                                }
                            }

                            @Order(3100)
                            public class NewMenu extends AbstractMenu {

                                @Override
                                protected Set<? extends IMenuType> getConfiguredMenuTypes() {
                                    return CollectionUtility.<IMenuType> hashSet(TableMenuType.EmptySpace);
                                }


                                @Override
                                protected String getConfiguredText() {
                                    return TEXTS.get("New");
                                }


                                @Override
                                protected boolean getConfiguredVisible() {
                                    return false;
                                }


                                @Override
                                protected void execAction() {
                                    newRowWithParent(getTable().getSelectedRow());
                                    // set parameter column editable
                                    getSutTcParameterTableField().getTable().getParameterNameColumn().setEditable(true);
                                    // set save botton visible
                                    getSutTcParameterTableField().getTable().getMenuByClass(SaveMenu.class).setVisible(true);
                                }

                                @Order(3110)
                                public class NewJsonObjectMenu extends AbstractMenu {
                                    @Override
                                    protected String getConfiguredText() {
                                        return TEXTS.get("JSONObject");
                                    }


                                    @Override
                                    protected Set<? extends IMenuType> getConfiguredMenuTypes() {
                                        return CollectionUtility.hashSet(TableMenuType.EmptySpace);
                                    }
                                }

                                @Order(3120)
                                public class NewJsonArrayMenu extends AbstractMenu {
                                    @Override
                                    protected String getConfiguredText() {
                                        return TEXTS.get("JSONArray");
                                    }


                                    @Override
                                    protected Set<? extends IMenuType> getConfiguredMenuTypes() {
                                        return CollectionUtility.hashSet(TableMenuType.EmptySpace);
                                    }
                                }

                                @Order(3130)
                                public class NewJsonElementMenu extends AbstractMenu {
                                    @Override
                                    protected String getConfiguredText() {
                                        return TEXTS.get("JSONElement");
                                    }


                                    @Override
                                    protected Set<? extends IMenuType> getConfiguredMenuTypes() {
                                        return CollectionUtility.hashSet(TableMenuType.EmptySpace);
                                    }
                                }

                                @Order(3140)
                                public class NewJsonValueMenu extends AbstractMenu {
                                    @Override
                                    protected String getConfiguredText() {
                                        return TEXTS.get("JSONValue");
                                    }


                                    @Override
                                    protected Set<? extends IMenuType> getConfiguredMenuTypes() {
                                        return CollectionUtility.hashSet(TableMenuType.EmptySpace);
                                    }
                                }
                            }

                            @Order(3200)
                            public class DeleteMenu extends AbstractMenu {

                                @Override
                                protected Set<? extends IMenuType> getConfiguredMenuTypes() {
                                    return CollectionUtility.<IMenuType> hashSet(TableMenuType.MultiSelection, TableMenuType.SingleSelection);
                                }


                                @Override
                                protected String getConfiguredText() {
                                    return TEXTS.get("DeleteMenu");
                                }


                                @Override
                                protected boolean getConfiguredVisible() {
                                    return false;
                                }


                                @Override
                                protected void execAction() {
                                    final List<ITableRow> rows = getSelectedRows();
                                    deleteRows(rows);
                                    // set save button visible
                                    getSutTcParameterTableField().getTable().getMenuByClass(SaveMenu.class).setVisible(true);
                                }
                            }

                            @Order(3900)
                            public class AbortMenu extends AbstractMenu {
                                @Override
                                protected String getConfiguredText() {
                                    return TEXTS.get("Abort");
                                }


                                @Override
                                protected Set<? extends IMenuType> getConfiguredMenuTypes() {
                                    return CollectionUtility.hashSet(TableMenuType.EmptySpace);
                                }


                                @Override
                                protected boolean getConfiguredVisible() {
                                    return false;
                                }


                                @Override
                                public byte getHorizontalAlignment() {
                                    return 1;
                                }


                                @Override
                                protected void execAction() {
                                    final SuTTcParameterTable tbl = getSutTcParameterTableField().getTable();

                                    tbl.getParameterNameColumn().setEditable(false);
                                    tbl.getParameterValueColumn().setEditable(false);

                                    tbl.getMenuByClass(AbortMenu.class).setVisible(false);
                                    tbl.getMenuByClass(SaveMenu.class).setVisible(false);
                                    tbl.getMenuByClass(NewMenu.class).setVisible(false);
                                    tbl.getMenuByClass(DeleteMenu.class).setVisible(false);

                                    tbl.getMenuByClass(EditMenu.class).setEnabled(true);

                                    // reload values if altered
                                    if (execIsSaveNeeded()) {
                                        getSutTcParameterTableField().reloadTableData();
                                    }
                                }
                            }

                            @Order(3910)
                            public class SaveMenu extends AbstractMenu {
                                @Override
                                protected String getConfiguredText() {
                                    return TEXTS.get("SaveChanges");
                                }


                                @Override
                                protected Set<? extends IMenuType> getConfiguredMenuTypes() {
                                    return CollectionUtility.hashSet(TableMenuType.EmptySpace);
                                }


                                @Override
                                protected boolean getConfiguredVisible() {
                                    return false;
                                }


                                @Override
                                public byte getHorizontalAlignment() {
                                    return 1;
                                }


                                @Override
                                protected void execAction() {
                                    final SuTTcParameterTable tbl = getSutTcParameterTableField().getTable();

                                    tbl.getParameterNameColumn().setEditable(false);
                                    tbl.getParameterValueColumn().setEditable(false);

                                    tbl.getMenuByClass(AbortMenu.class).setVisible(false);
                                    tbl.getMenuByClass(SaveMenu.class).setVisible(false);
                                    tbl.getMenuByClass(NewMenu.class).setVisible(false);
                                    tbl.getMenuByClass(DeleteMenu.class).setVisible(false);

                                    tbl.getMenuByClass(EditMenu.class).setEnabled(true);

                                    // ToDo: Save changes
                                    doSave();
                                    markSaved();
                                }
                            }
                        }
                    }

                    @Order(2000)
                    public class SutTcExtraParameterTableField extends AbstractTableField<SutTcExtraParameterTableField.SutTcExtraParameterTable> {
                        @Override
                        protected String getConfiguredLabel() {
                            return TEXTS.get("TcExtraParameterFiles");
                        }


                        @Override
                        protected int getConfiguredGridH() {
                            return 1;
                        }


                        @Override
                        protected int getConfiguredGridW() {
                            return 3;
                        }

                        @Order(2100)
                        public class SutTcExtraParameterTable extends AbstractTable {

                            @Override
                            protected boolean getConfiguredMultiSelect() {
                                // only a single row can be selected
                                return false;
                            }


                            public FileNameColumn getFileNameColumn() {
                                return getColumnSet().getColumnByClass(FileNameColumn.class);
                            }


                            @Override
                            protected void execRowsSelected(List<? extends ITableRow> rows) {
                                if (rows.size() == 1) {
                                    // set downlowad menu visible
                                    getMenuByClass(FileDownloadMenu.class).setVisible(true);
                                }
                                else {
                                    // hide download menu if no row is selected
                                    getMenuByClass(FileDownloadMenu.class).setVisible(false);
                                }
                            }

                            @Order(1000)
                            public class FileNameColumn extends AbstractStringColumn {
                                @Override
                                protected String getConfiguredHeaderText() {
                                    return TEXTS.get("FileName");
                                }


                                @Override
                                protected int getConfiguredWidth() {
                                    return 300;
                                }


                                @Override
                                protected int getConfiguredSortIndex() {
                                    // sort table by this column
                                    return 10;
                                }
                            }

                            @Order(2000)
                            public class FileUploadMenuMenu extends AbstractMenu {
                                @Override
                                protected String getConfiguredText() {
                                    return TEXTS.get("FileUpload");
                                }


                                @Override
                                protected Set<? extends IMenuType> getConfiguredMenuTypes() {
                                    return CollectionUtility.hashSet(TableMenuType.EmptySpace);
                                }


                                @Override
                                protected void execAction() {
                                    final FileChooser fileChooser = new FileChooser();
                                    final List<BinaryResource> files = fileChooser.startChooser();
                                    final ISuTCbService service = BEANS.get(ISuTCbService.class);
                                    files.forEach(file -> {
                                        if (service.copyUploadedTcExtraParameterFile(getSutId(), getCbId(), file)) {
                                            final ITableRow row = getTable().addRow(getTable().createRow());
                                            getTable().getFileNameColumn().setValue(row, file.getFilename());
                                        }
                                    });
                                    getTable().sort();
                                }
                            }

                            @Order(3000)
                            public class FileDownloadMenu extends AbstractMenu {
                                @Override
                                protected String getConfiguredText() {
                                    return TEXTS.get("FileDownload");
                                }


                                @Override
                                protected Set<? extends IMenuType> getConfiguredMenuTypes() {
                                    return CollectionUtility.hashSet(TableMenuType.EmptySpace);
                                }


                                @Override
                                protected boolean getConfiguredVisible() {
                                    return false;
                                }


                                @Override
                                protected void execAction() {
                                    // get the selected file name from the table
                                    final ITableRow row = getTable().getSelectedRow();
                                    if (row == null)
                                        // no row selected - nothing to do
                                        return;
                                    // get the content of the selected file
                                    final BinaryResource downloadFileResource = BEANS.get(ISuTCbService.class).getFileContent(getSutId(), getCbId(), getTable().getFileNameColumn().getValue(row));
                                    if (downloadFileResource.getContentLength() != -1) {
                                        getDesktop().openUri(downloadFileResource, OpenUriAction.DOWNLOAD);
                                    }
                                }
                            }
                        }


                        protected void loadTcExtraParamTable(String tsId) {
                            clearTcExtraParamTable();
                            loadExtraParams();
                            getTable().expandAll(null);
                        }


                        protected void clearTcExtraParamTable() {
                            getTable().deleteAllRows();
                        }


                        private void loadExtraParams() {
                            final ISuTCbService service = BEANS.get(ISuTCbService.class);
                            service.loadTcExtraParameterFiles(getSutId(), getActiveTsId()).forEach(fileName -> getTable().addRow().getCellForUpdate(getTable().getFileNameColumn()).setValue(fileName));
                        }
                    }
                }
            }
        }
    }

    public class CustomTile extends AbstractHtmlTile {

        @Override
        protected String getConfiguredContent() {
            return "<div title=>" + "<p name=\"" + TC_NAME_KW + "\">" + "</p>" + "<p name=\"" + TC_VERDICT_KW + "\">" + "</p>" + "</div>";
        }


        public void setTcTileContent(String tc, String tcVerdict) {
            setTcName(tc);
            setTcVerdict(tcVerdict);
            setTcVerdictColor(tcVerdict);
        }


        public void setTcName(String tcName) {
            final String titleReplaced = TILE_TITLE_CONTENT_PART.matcher(getContent()).replaceFirst("$1\"" + tcName + "\"$3");
            final String tcNameReplaced = TC_NAME_TILE_CONTENT_PART.matcher(titleReplaced).replaceFirst("$1" + tcName.substring(tcName.lastIndexOf('.') + 1) + "$3");
            setContent(tcNameReplaced);
        }


        public String getTcName() {
            return "";
        }


        public void setTcVerdict(String tcVerdict) {
            final String tcVerdictReplaced = TC_VERDICT_TILE_CONTENT_PART.matcher(getContent()).replaceFirst("$1" + tcVerdict + "$3");
            setContent(tcVerdictReplaced);
        }


        public String getTcVerdict() {
            return "";
        }


        public void setTcVerdictColor(String tcVerdict) {
            switch (tcVerdict) {
                case PASSED_VERDICT:
                    setCssClass("passed-tile");
                    break;
                case INCONCLUSIVE_VERDICT:
                    setCssClass("inconclusive-tile");
                    break;
                case FAILED_VERDICT:
                    setCssClass("failed-tile");
                    break;
                default:
                    setCssClass("default-tile");
            }

        }
    }

    public class ViewHandler extends AbstractFormHandler {

        @Override
        protected void execLoad() {
            loadTestSuites();
        }


        private void loadTestSuites() {
            final Set<String> irList = BEANS.get(ICbService.class).getIrForCb(cbId);
            final Set<String> tsList = BEANS.get(ITsService.class).getTsForIr(irList);

            final Accordion accordion = getAccordionField().getAccordion();
            // for an unknown reason, the accordion already has (a) group(s) but must be empty
            accordion.deleteAllGroups();

            // add all groups/testsuites with their test cases to the accordion
            tsList.stream().sorted().forEach(ts -> addTsGroupWithTcTiles(ts));

            // if only a single group then open it or collapse them all otherwise
            if (accordion.getGroupCount() == 1) {
                // load TC parameters
                final String tsId = accordion.getGroups().get(0).getTitle();
                setActiveTsId(tsId);
                getSutTcParameterTableField().loadTcParamTable(tsId);
                getSutExtraParameterTableField().loadTcExtraParamTable(tsId);
            }
            else {
                accordion.getGroups().forEach(group -> group.setCollapsed(true));
            }
        }

    }


    protected void addTsGroupWithTcTiles(String tsId) {

        final Accordion accordion = getAccordionField().getAccordion();
        final TileGroup group = accordion.new TileGroup();

        final Map<String, HashSet<String>> tcMap = BEANS.get(ITsService.class).getTcListForBadge(cbId);
        final List<ITile> tiles = new ArrayList<>();

        tcMap.getOrDefault(tsId, new HashSet<String>()).stream().sorted().forEachOrdered(tc -> {
            final CustomTile tile = new CustomTile();
            // set tile properties
            final GridData gridDataHints = tile.getGridDataHints();
            gridDataHints.weightX = 0;
            gridDataHints.widthInPixel = 250;
            gridDataHints.heightInPixel = 80;
            tile.setGridDataHints(gridDataHints);
            tile.setColorScheme(TileColorScheme.DEFAULT);
            // set content
            tile.setTcTileContent(tc, "PASSED");
            // add to group (TS)
            tiles.add(tile);
        });

        group.setTitle(tsId);
        group.getBody().setTiles(tiles);
        accordion.addGroup(group);
    }
}