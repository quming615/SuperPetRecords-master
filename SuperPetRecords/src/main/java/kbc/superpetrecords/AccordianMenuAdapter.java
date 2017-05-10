package kbc.superpetrecords;

import android.database.DataSetObserver;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.*;
import android.view.*;
import android.view.ViewGroup.*;
import android.view.animation.*;
import android.widget.*;
import android.content.*;
import android.content.res.*;

import java.io.InputStream;
import java.util.*;

/**
 * Created by kellanbc on 6/28/14.
 */

public class AccordianMenuAdapter implements ExpandableListAdapter {

    Context context;
    //ArrayList<Object> menus;
    AccordianMenu menu;

    private int lastMenuExpanded = 0;
    TextView header;
    ViewHolder holder;

    int layout_id, child_layout_id, group_layout_id;

    public AccordianMenuAdapter(Context context, int layout_id, int group_layout_id, int child_layout_id, AccordianMenu data) {
        this.context = context;
        this.layout_id = layout_id;
        this.child_layout_id = child_layout_id;
        this.group_layout_id = group_layout_id;
        menu = data;
        //AttributeParser.parseTypedArray(context, menus, R.array.navigation_drawer_menu_items);
    }

    public int getCount() {
        return menu.size();
    }

    public AccordianMenuItem getItem(int pos) {
        Log.d("AccordianMenuAdapter:getItem", "Position(" + pos + ")");
        if (pos < getCount()) {
            return menu.getItem(pos);
        } else {
            return null;
        }
    }

    public long getItemId(int pos) {
        return getItem(pos).getItemId();
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getGroupCount() {
        return menu.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        AccordianMenuItem item = menu.getItem(groupPosition);
        if (item.hasSubMenu()) {
            return item.getSubMenu().size();
        } else {
            return 0;
        }
    }

    @Override
    public AccordianMenuItem getGroup(int groupPosition) {
        return menu.getItem(groupPosition);
    }

    @Override
    public AccordianMenuItem getChild(int groupPosition, int childPosition) {
        AccordianMenuItem item = getGroup(groupPosition);
        if (item.hasSubMenu()) {
            return item.getSubMenu().getItem(childPosition);
        } else {
            return item;
        }
    }

    @Override
    public long getGroupId(int groupPosition) {
        return getGroup(groupPosition).getItemId();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return getChild(groupPosition, childPosition).getItemId();
    }


    @Override
    public boolean hasStableIds() {
        return true;
    }


    @Override
    public View getGroupView(final int groupPosition, final boolean isExpanded, View convertView, ViewGroup parent) {
        AccordianMenuItem item = getGroup(groupPosition);
        int id = (int) getGroupId(groupPosition);

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            holder = new ViewHolder();
            convertView = inflater.inflate(group_layout_id, parent, false);
            holder.icon = (ImageView) convertView.findViewById(R.id.dropdown_header_icon);
            holder.text = (TextView) convertView.findViewById(R.id.dropdown_header);
            convertView.setTag(holder);
        } else {
            Log.d("AccordianMenu:getGroupView", "id(" + id + ")");
            holder = (ViewHolder) convertView.getTag();
        }

        holder.icon.setImageDrawable(item.getIcon());
        holder.text.setText(item.getTitle());

        ((ExpandableListView) parent).setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
              @Override
              public boolean onGroupClick(ExpandableListView parent, View view, int groupPosition, long id) {
                  Toast.makeText(context, "OnItemClick(" + groupPosition + "): isExpanded(" + isExpanded + ")", Toast.LENGTH_SHORT).show();

                  int count = parent.getExpandableListAdapter().getChildrenCount(groupPosition);
                  int height = view.getLayoutParams().height;


                  if (!parent.isGroupExpanded(groupPosition)) {
                      //Log.d("setOnItemClickListener", "Expand: " + groupPosition);
                      /*
                      if (count > 1) {
                          int new_height = height * count;
                          //Log.d("AccordianMenu--Position(" + groupPosition + "," + childPosition + ")", "height(" + height + "), count(" + count + "), new_height(" + new_height + ")");
                          Animation anidelta = new AnimateLayoutParams(parent, view, new_height);
                          anidelta.setDuration(1000);
                          parent.startAnimation(anidelta);
                      } else if (count > 1) {
                          Animation anidelta = new AnimateLayoutParams(parent, view, height);
                          anidelta.setDuration(1000);
                          parent.startAnimation(anidelta);
                      }
                      */
                      parent.expandGroup(groupPosition, true);
                      if (lastMenuExpanded != 0) parent.collapseGroup(lastMenuExpanded);
                      lastMenuExpanded = groupPosition;
                  } else {
                      //Log.d("setOnItemClickListener", "Collapse: " + groupPosition);
                      parent.collapseGroup(groupPosition);
                  }
                  return true;
              }
        });


        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        AccordianMenuItem item = getChild(groupPosition, childPosition);
        LayoutInflater inflater = LayoutInflater.from(context);
        int id = (int) getChildId(groupPosition, childPosition);

        if (item.hasSubMenu()) {
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(layout_id, parent, false);
                AccordianMenuAdapter submenu_adapter = new AccordianMenuAdapter(context, layout_id, group_layout_id, child_layout_id, item.getSubMenu());

                ((ExpandableListView) convertView).setAdapter(submenu_adapter);
                convertView.setTag((int) getChildId(groupPosition, childPosition), holder);
            } else {
                Log.d("AccordianMenu:getChildView", "id(" + id + ")");
                holder = (ViewHolder) convertView.getTag();
            }

            ((ExpandableListView) convertView).setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override public boolean onChildClick(ExpandableListView parent, View view, int groupPosition, int childPosition, long id) {
                    Toast.makeText(context, "Recursive Click: Group(" + groupPosition + "), Child(" + childPosition + ")", Toast.LENGTH_SHORT).show();
                    int count = parent.getAdapter().getCount();
                    int height = view.getLayoutParams().height;

                    if (count > 1) {
                        int new_height = height * count;
                        //Log.d("AccordianMenu--Position(" + groupPosition + "," + childPosition + ")", "height(" + height + "), count(" + count + "), new_height(" + new_height + ")");
                        Animation anidelta = new AnimateLayoutParams(parent, view, new_height);
                        anidelta.setDuration(1000);
                        parent.startAnimation(anidelta);
                    } else if (count > 1) {
                        Animation anidelta = new AnimateLayoutParams(parent, view, height);
                        anidelta.setDuration(1000);
                        parent.startAnimation(anidelta);
                    }
                    return true;
                }
            });

        } else {
            String setting_text = item.getTitle().toString();
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(child_layout_id, parent, false);
                holder.text = (TextView) convertView.findViewById(R.id.dropdown_text);
                convertView.setTag(holder);
            } else {
                Log.d("AccordianMenu:getChildView", "id(" + id + ")");
                holder = (ViewHolder) convertView.getTag();
            }
            holder.text.setText(setting_text);

            ((ExpandableListView) parent).setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override public boolean onChildClick(ExpandableListView parent, View view, int groupPosition, int childPosition, long id) {
                    AccordianMenuItem item = getChild(groupPosition, childPosition);
                    if (item.clickListener != null) {
                        item.clickListener.onMenuItemClick(item);
                    }

                    return true;
                }
            });


        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEmpty() {
        return (getGroupCount() > 0) ? false : true;
    }

    @Override
    public void onGroupExpanded(int groupPosition) {

    }

    @Override
    public void onGroupCollapsed(int groupPosition) {

    }

    @Override
    public long getCombinedChildId(long groupId, long childId) {
        long combinedId = getCombinedGroupId(groupId) | childId;
        Log.d("AccordianMenuAdapter:getCombinedChildId", "group(" + groupId + "), child(" + childId + "), combined(" + combinedId + ")");
        return combinedId;
    }

    @Override
    public long getCombinedGroupId(long groupId) {
        return groupId << 32;
    }

    class ViewHolder {
        TextView text;
        ImageView icon;
    }

}
