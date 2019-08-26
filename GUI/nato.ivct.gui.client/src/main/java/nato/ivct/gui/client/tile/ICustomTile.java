package nato.ivct.gui.client.tile;

import org.eclipse.scout.rt.client.ui.tile.ITile;

public interface ICustomTile extends ITile {

  String PROP_LABEL = "label";

  String getLabel();

  void setLabel(String label);

  String getGroup();

  void setGroup(String group);
}