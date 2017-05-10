package kbc.superpetrecords;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * Created by kellanbc on 6/29/14.
 */
public class AccordianMenu implements Menu, SubMenu {

    private View headerView;
    private AccordianMenuItem parent = null;
    private ArrayList<AccordianMenuItem> menuItems = new ArrayList<>();

    private Context context;
    private boolean isQwerty;

    //SubMenu attributes;
    private CharSequence headerTitle;
    private int id;
    private Drawable icon, headerIcon;

    public AccordianMenu(Context context, int id) {
        this.id = id;
        this.context = context;
    }

    private AccordianMenu(Context context, CharSequence title) {
        this.context = context;
        this.setHeaderTitle(title);
    }

    private AccordianMenu(Context context, CharSequence title, int id) {
        this.context = context;
        this.setHeaderTitle(title);
        this.id = id;
    }

    public AccordianMenu(Context context, AccordianMenuItem parent) {
        this.parent = parent;
        this.context = context;
        menuItems = new ArrayList<>();
    }

    @Override public AccordianMenuItem add(CharSequence title) {
        AccordianMenuItem item = new AccordianMenuItem(this, title);
        menuItems.add(item);
        return item;
    }

    @Override public AccordianMenuItem add(int titleRes) {
        if (titleRes != 0) {
            return add(context.getResources().getString(titleRes));
        } else {
            return add("");
        }
    }

    public Context getContext() {
        return context;
    }
    /**
     * Add a new item to the menu. This item displays the given title for its
     * label.
     *
     * @param groupId The group identifier that this item should be part of.
     *                This can be used to define groups of items for batch state
     *                changes. Normally use {@link #NONE} if an item should not be in a
     *                group.
     * @param itemId  Unique item ID. Use {@link #NONE} if you do not need a
     *                unique ID.
     * @param order   The order for the item. Use {@link #NONE} if you do not care
     *                about the order. See {@link android.view.MenuItem#getOrder()}.
     * @param title   The text to display for the item.
     * @return The newly added menu item.
     */
    @Override
    public AccordianMenuItem add(int groupId, int itemId, int order, CharSequence title) {
        AccordianMenuItem item = new AccordianMenuItem(this, id, itemId, order, title);
        menuItems.add(order, item);
        return item;
    }

    @Override public AccordianMenuItem add(int groupId, int itemId, int order, int titleRes) {
        return add(groupId, itemId, order, context.getResources().getString(titleRes));
    }

    /**
     * Add a new sub-menu to the menu. This item displays the given title for
     * its label. To modify other attributes on the submenu's menu item, use
     * {@link android.view.SubMenu#getItem()}.
     *
     * @param title The text to display for the item.
     * @return The newly added sub-menu
     */
    @Override
    public AccordianMenu addSubMenu(CharSequence title) {
        AccordianMenu submenu = new AccordianMenu(context, title);
        AccordianMenuItem submenu_item = new AccordianMenuItem(this, title, submenu);
        submenu.setMenuParent(submenu_item);
        menuItems.add(submenu_item);
        return submenu;
    }

    /**
     * Add a new sub-menu to the menu. This item displays the given title for
     * its label. To modify other attributes on the submenu's menu item, use
     * {@link android.view.SubMenu#getItem()}.
     *
     * @param titleRes Resource identifier of title string.
     * @return The newly added sub-menu
     */
    @Override
    public AccordianMenu addSubMenu(int titleRes) {
        return addSubMenu(context.getResources().getString(titleRes));
    }

    public int getMenuId() {
        return id;
    }

    /**
     * Add a new sub-menu to the menu. This item displays the given
     * <var>title</var> for its label. To modify other attributes on the
     * submenu's menu item, use {@link android.view.SubMenu#getItem()}.
     * <p/>
     * Note that you can only have one level of sub-menus, i.e. you cannnot add
     * a subMenu to a subMenu: An {@link UnsupportedOperationException} will be
     * thrown if you try.
     *
     * @param groupId The group identifier that this item should be part of.
     *                This can also be used to define groups of items for batch state
     *                changes. Normally use {@link #NONE} if an item should not be in a
     *                group.
     * @param itemId  Unique item ID. Use {@link #NONE} if you do not need a
     *                unique ID.
     * @param order   The order for the item. Use {@link #NONE} if you do not care
     *                about the order. See {@link android.view.MenuItem#getOrder()}.
     * @param title   The text to display for the item.
     * @return The newly added sub-menu
     */
    @Override
    public AccordianMenu addSubMenu(int groupId, int itemId, int order, CharSequence title) {
        AccordianMenu submenu = new AccordianMenu(context, title, itemId);
        AccordianMenuItem submenu_item = new AccordianMenuItem(this, id, itemId, order, title, submenu);
        submenu.setMenuParent(submenu_item);
        menuItems.add(order, submenu_item);
        return submenu;
    }

    private AccordianMenu setMenuParent(AccordianMenuItem parent) {
        this.parent = parent;
        return this;
    }
    /**
     * Variation on {@link #addSubMenu(int, int, int, CharSequence)} that takes
     * a string resource identifier for the title instead of the string itself.
     *
     * @param groupId  The group identifier that this item should be part of.
     *                 This can also be used to define groups of items for batch state
     *                 changes. Normally use {@link #NONE} if an item should not be in a group.
     * @param itemId   Unique item ID. Use {@link #NONE} if you do not need a unique ID.
     * @param order    The order for the item. Use {@link #NONE} if you do not care about the
     *                 order. See {@link android.view.MenuItem#getOrder()}.
     * @param titleRes Resource identifier of title string.
     * @return The newly added sub-menu
     */
    @Override
    public AccordianMenu addSubMenu(int groupId, int itemId, int order, int titleRes) {
        return addSubMenu(groupId, itemId, order, context.getResources().getString(titleRes));
    }

    /**
     * Add a group of menu items corresponding to actions that can be performed
     * for a particular Intent. The Intent is most often configured with a null
     * action, the data that the current activity is working with, and includes
     * either the {@link android.content.Intent#CATEGORY_ALTERNATIVE} or
     * {@link android.content.Intent#CATEGORY_SELECTED_ALTERNATIVE} to find activities that have
     * said they would like to be included as optional action. You can, however,
     * use any Intent you want.
     * <p/>
     * <p/>
     * See {@link android.content.pm.PackageManager#queryIntentActivityOptions}
     * for more * details on the <var>caller</var>, <var>specifics</var>, and
     * <var>intent</var> arguments. The list returned by that function is used
     * to populate the resulting menu items.
     * <p/>
     * <p/>
     * All of the menu items of possible options for the intent will be added
     * with the given group and id. You can use the group to control ordering of
     * the items in relation to other items in the menu. Normally this function
     * will automatically remove any existing items in the menu in the same
     * group and place a divider above and below the added items; this behavior
     * can be modified with the <var>flags</var> parameter. For each of the
     * generated items {@link android.view.MenuItem#setIntent} is called to associate the
     * appropriate Intent with the item; this means the activity will
     * automatically be started for you without having to do anything else.
     *
     * @param groupId          The group identifier that the items should be part of.
     *                         This can also be used to define groups of items for batch state
     *                         changes. Normally use {@link #NONE} if the items should not be in
     *                         a group.
     * @param itemId           Unique item ID. Use {@link #NONE} if you do not need a
     *                         unique ID.
     * @param order            The order for the items. Use {@link #NONE} if you do not
     *                         care about the order. See {@link android.view.MenuItem#getOrder()}.
     * @param caller           The current activity component name as defined by
     *                         queryIntentActivityOptions().
     * @param specifics        Specific items to place first as defined by
     *                         queryIntentActivityOptions().
     * @param intent           Intent describing the kinds of items to populate in the
     *                         list as defined by queryIntentActivityOptions().
     * @param flags            Additional options controlling how the items are added.
     * @param outSpecificItems Optional array in which to place the menu items
     *                         that were generated for each of the <var>specifics</var> that were
     *                         requested. Entries may be null if no activity was found for that
     *                         specific action.
     * @return The number of menu items that were added.
     * @see #FLAG_APPEND_TO_GROUP
     * @see android.view.MenuItem#setIntent
     * @see android.content.pm.PackageManager#queryIntentActivityOptions
     */
    @Override
    public int addIntentOptions(int groupId, int itemId, int order, ComponentName caller, Intent[] specifics, Intent intent, int flags, MenuItem[] outSpecificItems) {
        return 0;
    }

    /**
     * Remove the item with the given identifier.
     *
     * @param id The item to be removed.  If there is no item with this
     *           identifier, nothing happens.
     */
    @Override
    public void removeItem(int id) {

    }

    /**
     * Remove all items in the given group.
     *
     * @param groupId The group to be removed.  If there are no items in this
     *                group, nothing happens.
     */
    @Override
    public void removeGroup(int groupId) {

    }

    /**
     * Remove all existing items from the menu, leaving it empty as if it had
     * just been created.
     */
    @Override
    public void clear() {

    }

    /**
     * Control whether a particular group of items can show a check mark.  This
     * is similar to calling {@link android.view.MenuItem#setCheckable} on all of the menu items
     * with the given group identifier, but in addition you can control whether
     * this group contains a mutually-exclusive set items.  This should be called
     * after the items of the group have been added to the menu.
     *
     * @param group     The group of items to operate on.
     * @param checkable Set to true to allow a check mark, false to
     *                  disallow.  The default is false.
     * @param exclusive If set to true, only one item in this group can be
     *                  checked at a time; checking an item will automatically
     *                  uncheck all others in the group.  If set to false, each
     *                  item can be checked independently of the others.
     * @see android.view.MenuItem#setCheckable
     * @see android.view.MenuItem#setChecked
     */
    @Override
    public void setGroupCheckable(int group, boolean checkable, boolean exclusive) {

    }

    /**
     * Show or hide all menu items that are in the given group.
     *
     * @param group   The group of items to operate on.
     * @param visible If true the items are visible, else they are hidden.
     * @see android.view.MenuItem#setVisible
     */
    @Override
    public void setGroupVisible(int group, boolean visible) {

    }

    /**
     * Enable or disable all menu items that are in the given group.
     *
     * @param group   The group of items to operate on.
     * @param enabled If true the items will be enabled, else they will be disabled.
     * @see android.view.MenuItem#setEnabled
     */
    @Override
    public void setGroupEnabled(int group, boolean enabled) {

    }

    /**
     * Return whether the menu currently has item items that are visible.
     *
     * @return True if there is one or more item visible,
     * else false.
     */
    @Override
    public boolean hasVisibleItems() {
        return false;
    }

    /**
     * Return the menu item with a particular identifier.
     *
     * @param id The identifier to find.
     * @return The menu item object, or null if there is no item with
     * this identifier.
     */
    @Override
    public AccordianMenuItem findItem(int id) {
        return null;
    }

    /**
     * Get the number of items in the menu.  Note that this will change any
     * times items are added or removed from the menu.
     *
     * @return The item count.
     */
    @Override
    public int size() {
        return menuItems.size();
    }

    /**
     * Gets the menu item at the given index.
     *
     * @param index The index of the menu item to return.
     * @return The menu item.
     * @throws IndexOutOfBoundsException when {@code index < 0 || >= size()}
     */
    @Override
    public AccordianMenuItem getItem(int index) {
        return menuItems.get(index);
    }

    /**
     * Closes the menu, if open.
     */
    @Override
    public void close() {

    }

    /**
     * Execute the menu item action associated with the given shortcut
     * character.
     *
     * @param keyCode The keycode of the shortcut key.
     * @param event   Key event message.
     * @param flags   Additional option flags or 0.
     * @return If the given shortcut exists and is shown, returns
     * true; else returns false.
     * @see #FLAG_PERFORM_NO_CLOSE
     */
    @Override
    public boolean performShortcut(int keyCode, KeyEvent event, int flags) {
        return false;
    }

    /**
     * Is a keypress one of the defined shortcut keys for this window.
     *
     * @param keyCode the key code from {@link android.view.KeyEvent} to check.
     * @param event   the {@link android.view.KeyEvent} to use to help check.
     */
    @Override
    public boolean isShortcutKey(int keyCode, KeyEvent event) {
        return false;
    }

    /**
     * Execute the menu item action associated with the given menu identifier.
     *
     * @param id    Identifier associated with the menu item.
     * @param flags Additional option flags or 0.
     * @return If the given identifier exists and is shown, returns
     * true; else returns false.
     * @see #FLAG_PERFORM_NO_CLOSE
     */
    @Override
    public boolean performIdentifierAction(int id, int flags) {
        return false;
    }

    /**
     * Control whether the menu should be running in qwerty mode (alphabetic
     * shortcuts) or 12-key mode (numeric shortcuts).
     *
     * @param isQwerty If true the menu will use alphabetic shortcuts; else it
     *                 will use numeric shortcuts.
     */
    @Override
    public void setQwertyMode(boolean isQwerty) {
        this.isQwerty = isQwerty;
    }

    /* SubMenu Interfaces */

    /**
     * Sets the submenu header's title to the title given in <var>titleRes</var>
     * resource identifier.
     *
     * @param titleRes The string resource identifier used for the title.
     * @return This SubMenu so additional setters can be called.
     */
    @Override
    public AccordianMenu setHeaderTitle(int titleRes) {
        if (titleRes > 0) {
            return setHeaderTitle(context.getResources().getString(titleRes));
        } else {
            return this;
        }
    }

    public View getHeaderView() {
        return headerView;
    }

    public CharSequence getHeaderTitle() {
        return headerTitle;
    }

    public Drawable getHeaderIcon() {
        return headerIcon;
    }


    /**
     * Sets the submenu header's title to the title given in <var>title</var>.
     *
     * @param title The character sequence used for the title.
     * @return This SubMenu so additional setters can be called.
     */
    @Override
    public AccordianMenu setHeaderTitle(CharSequence title) {
        this.headerTitle = title;
        return this;
    }

    /**
     * Sets the submenu header's icon to the icon given in <var>iconRes</var>
     * resource id.
     *
     * @param iconRes The resource identifier used for the icon.
     * @return This SubMenu so additional setters can be called.
     */
    @Override
    public AccordianMenu setHeaderIcon(int iconRes) {
        if (iconRes > 0) {
            return setHeaderIcon(context.getResources().getDrawable(iconRes));
        } else {
            return this;
        }
    }

    /**
     * Sets the submenu header's icon to the icon given in <var>icon</var>
     * {@link android.graphics.drawable.Drawable}.
     *
     * @param icon The {@link android.graphics.drawable.Drawable} used for the icon.
     * @return This SubMenu so additional setters can be called.
     */
    @Override
    public AccordianMenu setHeaderIcon(Drawable icon) {
        this.headerIcon = icon;
        return this;
    }

    /**
     * Sets the header of the submenu to the {@link android.view.View} given in
     * <var>view</var>. This replaces the header title and icon (and those
     * replace this).
     *
     * @param view The {@link android.view.View} used for the header.
     * @return This SubMenu so additional setters can be called.
     */
    @Override
    public AccordianMenu setHeaderView(View view) {
        this.headerView = view;
        return this;
    }

    /**
     * Clears the header of the submenu.
     */
    @Override
    public void clearHeader() {
        headerView = null;
        headerIcon = null;
        headerTitle = null;
    }

    /**
     * Change the icon associated with this submenu's item in its parent menu.
     *
     * @param iconRes The new icon (as a resource ID) to be displayed.
     * @return This SubMenu so additional setters can be called.
     * @see android.view.MenuItem#setIcon(int)
     */
    @Override
    public AccordianMenu setIcon(int iconRes) {
        setIcon(context.getResources().getDrawable(iconRes));
        return this;
    }

    /**
     * Change the icon associated with this submenu's item in its parent menu.
     *
     * @param icon The new icon (as a Drawable) to be displayed.
     * @return This SubMenu so additional setters can be called.
     * @see android.view.MenuItem#setIcon(android.graphics.drawable.Drawable)
     */
    @Override
    public AccordianMenu setIcon(Drawable icon) {
        this.icon = icon;
        return this;
    }

    /**
     * Gets the {@link android.view.MenuItem} that represents this submenu in the parent
     * menu.  Use this for setting additional item attributes.
     *
     * @return The {@link android.view.MenuItem} that launches the submenu when invoked.
     */
    @Override
    public AccordianMenuItem getItem() {
        return parent;
    }
}
