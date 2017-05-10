package kbc.superpetrecords;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.*;

import java.util.ArrayList;

/**
 * Created by kellanbc on 6/29/14.
 */

public class AccordianMenuItem implements MenuItem {
    private Context context;

    private AccordianMenu submenu = null;
    private AccordianMenu parent;
    private int itemId, groupId, order;
    private CharSequence title, titleCondensed;
    private Drawable icon;
    private Intent intent;
    private boolean checkable, checked, visible, enabled;
    private char alphabeticShortcut, numericShortcut;

    private int actionEnum = SHOW_AS_ACTION_NEVER;
    public OnMenuItemClickListener clickListener;

    private static final int CHECKABLE = 0x00000001;
    private static final int CHECKED = 0x00000002;
    private static final int EXCLUSIVE = 0x00000004;
    private static final int HIDDEN = 0x00000008;
    private static final int ENABLED = 0x00000010;
    private int flags = ENABLED;

    public AccordianMenuItem(AccordianMenu parent, int groupId, int itemId, int order, CharSequence title) {
        Log.d("AccordianMenuItem(" + title.toString() + ")", "itemId(" + itemId + "), groupId(" + groupId + ")");

        this.parent = parent;
        context = parent.getContext();
        this.title = title;
        this.itemId = itemId;
        this.groupId = groupId;
    }

    public AccordianMenuItem(AccordianMenu parent, CharSequence title) {
        this.parent = parent;
        context = parent.getContext();

        this.title = title;
    }

    public AccordianMenuItem(AccordianMenu parent, int groupId, int itemId, int order, CharSequence title, AccordianMenu submenu) {
        Log.d("AccordianMenuItem(" + title.toString() + ")", "itemId(" + itemId + "), groupId(" + groupId + ")");

        this.parent = parent;
        context = parent.getContext();

        this.title = title;
        this.itemId = itemId;
        this.groupId = groupId;
        this.submenu = submenu;
    }

    public AccordianMenuItem(AccordianMenu parent, CharSequence title, AccordianMenu submenu) {
        this.parent = parent;
        context = parent.getContext();

        this.title = title;
        this.submenu = submenu;
    }

    @Override public int getItemId() {
        return itemId;
    }

    @Override
    public int getGroupId() {
        if (hasSubMenu()) {
            return itemId;
        } else {
            return groupId;
        }
    }

    /**
     * Return the category and order within the category of this item. This
     * item will be shown before all items (within its category) that have
     * order greater than this value.
     * <p/>
     * An order integer contains the item's category (the upper bits of the
     * integer; set by or/add the category with the order within the
     * category) and the ordering of the item within that category (the
     * lower bits). Example categories are {@link android.view.Menu#CATEGORY_SYSTEM},
     * {@link android.view.Menu#CATEGORY_SECONDARY}, {@link android.view.Menu#CATEGORY_ALTERNATIVE},
     * {@link android.view.Menu#CATEGORY_CONTAINER}. See {@link android.view.Menu} for a full list.
     *
     * @return The order of this item.
     */
    @Override public int getOrder() {
        return order;
    }

    /**
     * Change the title associated with this item.
     *
     * @param title The new text to be displayed.
     * @return This Item so additional setters can be called.
     */
    @Override
    public AccordianMenuItem setTitle(CharSequence title) {
        this.title = title;
        return this;
    }

    /**
     * Change the title associated with this item.
     * <p/>
     * Some menu types do not sufficient space to show the full title, and
     * instead a condensed title is preferred. See {@link android.view.Menu} for more
     * information.
     *
     * @param title The resource id of the new text to be displayed.
     * @return This Item so additional setters can be called.
     * @see #setTitleCondensed(CharSequence)
     */
    @Override
    public AccordianMenuItem setTitle(int title) {
        if (title != 0) {
            this.title = context.getResources().getString(title);
        }
        return this;
    }

    @Override
    public CharSequence getTitle() {
        return title;
    }

    @Override
    public AccordianMenuItem setTitleCondensed(CharSequence title) {
        this.titleCondensed = title;
        return this;
    }

    /**
     * Retrieve the current condensed title of the item. If a condensed
     * title was never set, it will return the normal title.
     *
     * @return The condensed title, if it exists.
     * Otherwise the normal title.
     */
    @Override
    public CharSequence getTitleCondensed() {
        return titleCondensed;
    }

    /**
     * Change the icon associated with this item. This icon will not always be
     * shown, so the title should be sufficient in describing this item. See
     * {@link android.view.Menu} for the menu types that support icons.
     *
     * @param icon The new icon (as a Drawable) to be displayed.
     * @return This Item so additional setters can be called.
     */
    @Override
    public AccordianMenuItem setIcon(Drawable icon) {
        this.icon = icon;
        return this;
    }

    /**
     * Change the icon associated with this item. This icon will not always be
     * shown, so the title should be sufficient in describing this item. See
     * {@link android.view.Menu} for the menu types that support icons.
     * <p/>
     * This method will set the resource ID of the icon which will be used to
     * lazily get the Drawable when this item is being shown.
     *
     * @param iconRes The new icon (as a resource ID) to be displayed.
     * @return This Item so additional setters can be called.
     */
    @Override
    public AccordianMenuItem setIcon(int iconRes) {
        if (iconRes != 0) {
            this.icon = context.getResources().getDrawable(iconRes);
        }
        return this;
    }

    /**
     * Returns the icon for this item as a Drawable (getting it from resources if it hasn't been
     * loaded before).
     *
     * @return The icon as a Drawable.
     */
    @Override
    public Drawable getIcon() {
        return icon;
    }

    /**
     * Change the Intent associated with this item.  By default there is no
     * Intent associated with a menu item.  If you set one, and nothing
     * else handles the item, then the default behavior will be to call
     * {@link android.content.Context#startActivity} with the given Intent.
     * <p/>
     * <p>Note that setIntent() can not be used with the versions of
     * {@link android.view.Menu#add} that take a Runnable, because {@link Runnable#run}
     * does not return a value so there is no way to tell if it handled the
     * item.  In this case it is assumed that the Runnable always handles
     * the item, and the intent will never be started.
     *
     * @param intent The Intent to associated with the item.  This Intent
     *               object is <em>not</em> copied, so be careful not to
     *               modify it later.
     * @return This Item so additional setters can be called.
     * @see #getIntent
     */
    @Override
    public AccordianMenuItem setIntent(Intent intent) {
        return this;
    }

    /**
     * Return the Intent associated with this item.  This returns a
     * reference to the Intent which you can change as desired to modify
     * what the Item is holding.
     *
     * @return Returns the last value supplied to {@link #setIntent}, or
     * null.
     * @see #setIntent
     */
    @Override
    public Intent getIntent() {
        return intent;
    }

    /**
     * Change both the numeric and alphabetic shortcut associated with this
     * item. Note that the shortcut will be triggered when the key that
     * generates the given character is pressed alone or along with with the alt
     * key. Also note that case is not significant and that alphabetic shortcut
     * characters will be displayed in lower case.
     * <p/>
     * See {@link android.view.Menu} for the menu types that support shortcuts.
     *
     * @param numericChar The numeric shortcut key. This is the shortcut when
     *                    using a numeric (e.g., 12-key) keyboard.
     * @param alphaChar   The alphabetic shortcut key. This is the shortcut when
     *                    using a keyboard with alphabetic keys.
     * @return This Item so additional setters can be called.
     */
    @Override
    public AccordianMenuItem setShortcut(char numericChar, char alphaChar) {
        return setNumericShortcut(numericChar).setAlphabeticShortcut(alphaChar);
    }

    /**
     * Change the numeric shortcut associated with this item.
     * <p/>
     * See {@link android.view.Menu} for the menu types that support shortcuts.
     *
     * @param numericChar The numeric shortcut key.  This is the shortcut when
     *                    using a 12-key (numeric) keyboard.
     * @return This Item so additional setters can be called.
     */
    @Override
    public AccordianMenuItem setNumericShortcut(char numericChar) {
        this.numericShortcut = numericChar;
        return this;
    }

    /**
     * Return the char for this menu item's numeric (12-key) shortcut.
     *
     * @return Numeric character to use as a shortcut.
     */
    @Override
    public char getNumericShortcut() {
        return this.numericShortcut;
    }

    /**
     * Change the alphabetic shortcut associated with this item. The shortcut
     * will be triggered when the key that generates the given character is
     * pressed alone or along with with the alt key. Case is not significant and
     * shortcut characters will be displayed in lower case. Note that menu items
     * with the characters '\b' or '\n' as shortcuts will get triggered by the
     * Delete key or Carriage Return key, respectively.
     * <p/>
     * See {@link android.view.Menu} for the menu types that support shortcuts.
     *
     * @param alphaChar The alphabetic shortcut key. This is the shortcut when
     *                  using a keyboard with alphabetic keys.
     * @return This Item so additional setters can be called.
     */
    @Override
    public AccordianMenuItem setAlphabeticShortcut(char alphaChar) {
        this.alphabeticShortcut = alphaChar;
        return this;
    }

    @Override
    public char getAlphabeticShortcut() {
        return alphabeticShortcut;
    }

    public boolean isCheckable() {
        return (flags & CHECKABLE) != 0;
    }

    public boolean isChecked() {
        return (flags & CHECKED) != 0;
    }

    public boolean isEnabled() {
        return (flags & ENABLED) != 0;
    }

    public boolean isVisible() {
        return (flags & HIDDEN) == 0;
    }

    public AccordianMenuItem setVisible(boolean visible) {
        flags = (flags & ~HIDDEN) | (visible ? 0 : HIDDEN);
        return this;
    }

    public AccordianMenuItem setCheckable(boolean checkable) {
        flags = (flags & ~CHECKABLE) | (checkable ? CHECKABLE : 0);
        return this;
    }

    public AccordianMenuItem setExclusiveCheckable(boolean exclusive) {
        flags = (flags & ~EXCLUSIVE) | (exclusive ? EXCLUSIVE : 0);
        return this;
    }

    public AccordianMenuItem setChecked(boolean checked) {
        flags = (flags & ~CHECKED) | (checked ? CHECKED : 0);
        return this;
    }

    public AccordianMenuItem setEnabled(boolean enabled) {
        flags = (flags & ~ENABLED) | (enabled ? ENABLED : 0);
        return this;
    }

   @Override public boolean hasSubMenu() {
        return (submenu == null) ? false : true;
    }

    @Override public AccordianMenu getSubMenu() {
        if (hasSubMenu())
            return submenu;
        else
            return null;
    }

    /**
     * Set a custom listener for invocation of this menu item. In most
     * situations, it is more efficient and easier to use
     * {@link Activity#onOptionsItemSelected(android.view.MenuItem)} or
     * {@link Activity#onContextItemSelected(android.view.MenuItem)}.
     *
     * @param menuItemClickListener The object to receive invokations.
     * @return This Item so additional setters can be called.
     * @see Activity#onOptionsItemSelected(android.view.MenuItem)
     * @see Activity#onContextItemSelected(android.view.MenuItem)
     */


    @Override
    public AccordianMenuItem setOnMenuItemClickListener(OnMenuItemClickListener menuItemClickListener) {
        clickListener = menuItemClickListener;
        return this;
    }

    /**
     * Gets the extra information linked to this menu item.  This extra
     * information is set by the View that added this menu item to the
     * menu.
     *
     * @return The extra information linked to the View that added this
     * menu item to the menu. This can be null.
     * //@see OnCreateContextMenuListener
     */
    @Override
    public ContextMenu.ContextMenuInfo getMenuInfo() {
        return null;
    }

    /**
     * Sets how this item should display in the presence of an Action Bar.
     * The parameter actionEnum is a flag set. One of {@link #SHOW_AS_ACTION_ALWAYS},
     * {@link #SHOW_AS_ACTION_IF_ROOM}, or {@link #SHOW_AS_ACTION_NEVER} should
     * be used, and you may optionally OR the value with {@link #SHOW_AS_ACTION_WITH_TEXT}.
     * SHOW_AS_ACTION_WITH_TEXT requests that when the item is shown as an action,
     * it should be shown with a text label.
     *
     * @param actionEnum How the item should display. One of
     *                   {@link #SHOW_AS_ACTION_ALWAYS}, {@link #SHOW_AS_ACTION_IF_ROOM}, or
     *                   {@link #SHOW_AS_ACTION_NEVER}. SHOW_AS_ACTION_NEVER is the default.
     * @see android.app.ActionBar
     * @see #setActionView(android.view.View)
     */

    @Override
    public void setShowAsAction(int actionEnum) {
        this.actionEnum = actionEnum;
    }

    /**
     * Sets how this item should display in the presence of an Action Bar.
     * The parameter actionEnum is a flag set. One of {@link #SHOW_AS_ACTION_ALWAYS},
     * {@link #SHOW_AS_ACTION_IF_ROOM}, or {@link #SHOW_AS_ACTION_NEVER} should
     * be used, and you may optionally OR the value with {@link #SHOW_AS_ACTION_WITH_TEXT}.
     * SHOW_AS_ACTION_WITH_TEXT requests that when the item is shown as an action,
     * it should be shown with a text label.
     * <p/>
     * <p>Note: This method differs from {@link #setShowAsAction(int)} only in that it
     * returns the current MenuItem instance for call chaining.
     *
     * @param actionEnum How the item should display. One of
     *                   {@link #SHOW_AS_ACTION_ALWAYS}, {@link #SHOW_AS_ACTION_IF_ROOM}, or
     *                   {@link #SHOW_AS_ACTION_NEVER}. SHOW_AS_ACTION_NEVER is the default.
     * @return This MenuItem instance for call chaining.
     * @see android.app.ActionBar
     * @see #setActionView(android.view.View)
     */
    @Override
    public AccordianMenuItem setShowAsActionFlags(int actionEnum) {
        this.actionEnum = actionEnum;
        return this;
    }

    /**
     * Set an action view for this menu item. An action view will be displayed in place
     * of an automatically generated menu item element in the UI when this item is shown
     * as an action within a parent.
     * <p>
     * <strong>Note:</strong> Setting an action view overrides the action provider
     * set via {@link #setActionProvider(android.view.ActionProvider)}.
     * </p>
     *
     * @param view View to use for presenting this item to the user.
     * @return This Item so additional setters can be called.
     * @see #setShowAsAction(int)
     */
    @Override
    public AccordianMenuItem setActionView(View view) {
        return null;
    }

    /**
     * Set an action view for this menu item. An action view will be displayed in place
     * of an automatically generated menu item element in the UI when this item is shown
     * as an action within a parent.
     * <p>
     * <strong>Note:</strong> Setting an action view overrides the action provider
     * set via {@link #setActionProvider(android.view.ActionProvider)}.
     * </p>
     *
     * @param resId Layout resource to use for presenting this item to the user.
     * @return This Item so additional setters can be called.
     * @see #setShowAsAction(int)
     */
    @Override
    public AccordianMenuItem setActionView(int resId) {
        return null;
    }

    /**
     * Returns the currently set action view for this menu item.
     *
     * @return This item's action view
     * @see #setActionView(android.view.View)
     * @see #setShowAsAction(int)
     */
    @Override
    public View getActionView() {
        return null;
    }

    /**
     * Sets the {@link android.view.ActionProvider} responsible for creating an action view if
     * the item is placed on the action bar. The provider also provides a default
     * action invoked if the item is placed in the overflow menu.
     * <p>
     * <strong>Note:</strong> Setting an action provider overrides the action view
     * set via {@link #setActionView(int)} or {@link #setActionView(android.view.View)}.
     * </p>
     *
     * @param actionProvider The action provider.
     * @return This Item so additional setters can be called.
     * @see android.view.ActionProvider
     */
    @Override
    public AccordianMenuItem setActionProvider(ActionProvider actionProvider) {
        return null;
    }

    /**
     * Gets the {@link android.view.ActionProvider}.
     *
     * @return The action provider.
     * @see android.view.ActionProvider
     * @see #setActionProvider(android.view.ActionProvider)
     */
    @Override
    public ActionProvider getActionProvider() {
        return null;
    }

    /**
     * Expand the action view associated with this menu item.
     * The menu item must have an action view set, as well as
     * the showAsAction flag {@link #SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW}.
     * If a listener has been set using {@link #setOnActionExpandListener(android.view.MenuItem.OnActionExpandListener)}
     * it will have its {@link android.view.MenuItem.OnActionExpandListener#onMenuItemActionExpand(android.view.MenuItem)}
     * method invoked. The listener may return false from this method to prevent expanding
     * the action view.
     *
     * @return true if the action view was expanded, false otherwise.
     */
    @Override
    public boolean expandActionView() {
        return false;
    }

    /**
     * Collapse the action view associated with this menu item.
     * The menu item must have an action view set, as well as the showAsAction flag
     * {@link #SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW}. If a listener has been set using
     * {@link #setOnActionExpandListener(android.view.MenuItem.OnActionExpandListener)} it will have its
     * {@link android.view.MenuItem.OnActionExpandListener#onMenuItemActionCollapse(android.view.MenuItem)} method invoked.
     * The listener may return false from this method to prevent collapsing the action view.
     *
     * @return true if the action view was collapsed, false otherwise.
     */
    @Override
    public boolean collapseActionView() {
        return false;
    }

    /**
     * Returns true if this menu item's action view has been expanded.
     *
     * @return true if the item's action view is expanded, false otherwise.
     * @see #expandActionView()
     * @see #collapseActionView()
     * @see #SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW
     * @see android.view.MenuItem.OnActionExpandListener
     */
    @Override
    public boolean isActionViewExpanded() {
        return false;
    }

    /**
     * Set an {@link android.view.MenuItem.OnActionExpandListener} on this menu item to be notified when
     * the associated action view is expanded or collapsed. The menu item must
     * be configured to expand or collapse its action view using the flag
     * {@link #SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW}.
     *
     * @param listener Listener that will respond to expand/collapse events
     * @return This menu item instance for call chaining
     */
    @Override
    public AccordianMenuItem setOnActionExpandListener(OnActionExpandListener listener) {
        return null;
    }


}
