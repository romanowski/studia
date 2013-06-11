package pl.flexable.harmonix.container {
  import flash.display.DisplayObject;
  import flash.events.Event;
  import flash.events.KeyboardEvent;
  import flash.events.MouseEvent;
  import flash.filters.GlowFilter;

  import mx.collections.ArrayCollection;
  import mx.core.ClassFactory;
  import mx.core.DragSource;
  import mx.core.LayoutContainer;
  import mx.core.UIComponent;
  import mx.events.DragEvent;
  import mx.managers.DragManager;
  import mx.managers.IFocusManagerComponent;

  import pl.flexable.harmonix.clipboard.ClipboardClient;
  import pl.flexable.harmonix.interfaces.IClipboardClient;
  import pl.flexable.harmonix.layout.AbstractLayout;


  /**
   *
   */
  [Event(name="layoutChanged")]


  /**
   *
   * @author Mateusz
   *
   */
  public class LayoutContainer extends mx.core.LayoutContainer implements IClipboardClient, IFocusManagerComponent {

    /**
     *
     */
    public var selectionColor : uint = 0x666666;

    /**
     *
     */
    public var dragEnabled : Boolean = true;

    /**
     *
     */
    public var dropEnabled : Boolean = true;

    /**
     *
     */
    public var deleteEnabled : Boolean = false;

    /**
     *
     */
    protected var selectedChildren : ArrayCollection = new ArrayCollection();

    /**
     *
     */
    protected var clipboardClient : ClipboardClient;

    /**
     *
     */
    private var _layoutClass : Class;

    /**
     *
     */
    private var _verticalGap : int = 20;

    /**
     *
     */
    private var _horizontalGap : int = 20;

    /**
     *
     */
    private var _indexedChildren : Array = [];

    /**
     *
     */
    private var _selectable : Boolean = true;

    /**
     *
     */
    private var _allowMultipleSelection : Boolean = false;

    /**
     *
     */
    private var _clipboardEnabled : Boolean = false;


    /**
     *
     */
    private var _itemWidth : Number = 0;

    /**
     *
     */
    private var _itemHeight : Number = 0;


    /**
     *
     *
     */
    public function LayoutContainer() {

      super();

      layoutObject = new AbstractLayout();
      layoutObject.target = this;

      clipboardClient = new ClipboardClient(this);

      focusEnabled = true;

      addEventListener(MouseEvent.CLICK, onClick);
      addEventListener(DragEvent.DRAG_ENTER, onDragEnter);
      addEventListener(DragEvent.DRAG_DROP, onDragDrop);
      addEventListener(KeyboardEvent.KEY_DOWN, onKeyDown);
    }


    /**
     *
     * @param value
     *
     */
    public function set itemWidth(value : Number) : void {
      if (value != _itemWidth) {
        _itemWidth = value;
        if (layoutObject && layoutObject is AbstractLayout) {
          AbstractLayout(layoutObject).itemWidth = itemWidth;
          invalidateDisplayList();
        }
      }
    }


    /**
     *
     * @return
     *
     */
    public function get itemWidth() : Number {
      return _itemWidth;
    }


    /**
     *
     * @param value
     *
     */
    public function set itemHeight(value : Number) : void {
      if (value != _itemHeight) {
        _itemHeight = value;
        if (layoutObject && layoutObject is AbstractLayout) {
          AbstractLayout(layoutObject).itemHeight = itemHeight;
          invalidateDisplayList();
        }
      }
    }


    /**
     *
     * @return
     *
     */
    public function get itemHeight() : Number {
      return _itemHeight;
    }


    protected function onClick(event : MouseEvent) : void {
      this.setFocus();
      if (event.target == this) {
        unselectAllChildren();
      }
    }


    /**
     *
     * @param unscaledWidth
     * @param unscaledHeight
     *
     */
    override protected function updateDisplayList(unscaledWidth : Number, unscaledHeight : Number) : void {
      super.updateDisplayList(unscaledWidth, unscaledHeight);

      graphics.clear();
      graphics.beginFill(0xFFFFFF, 0.0001);
      graphics.drawRect(0, 0, unscaledWidth, unscaledHeight);
      graphics.endFill();
    }

    //************************************************
    //  IFocusManagerComponent impl
    //************************************************

    /**
     *
     */
    private var _focusEnabled : Boolean = true;

    /**
     *
     */
    private var _mouseFocusEnabled : Boolean = true;


    //************************************************
    //  ISelectableContainer impl
    //************************************************

    /**
     *
     * @param value
     *
     */
    public function set selectable(value : Boolean) : void {
      if (value != _selectable) {
        _selectable = value;
        dispatchEvent(new Event("selectableChange"));
      }
    }


    [Bindable("selectableChange")]
    /**
     *
     * @return
     *
     */
    public function get selectable() : Boolean {
      return _selectable;
    }


    /**
     *
     * @param value
     *
     */
    public function set allowMultipleSelection(value : Boolean) : void {
      if (value != _allowMultipleSelection) {
        _allowMultipleSelection = value;
        dispatchEvent(new Event("allowMultipleSelectionChange"));
      }
    }


    [Bindable("allowMultipleSelectionChange")]
    public function get allowMultipleSelection() : Boolean {
      return _allowMultipleSelection;
    }


    /**
     *
     * @return
     *
     */
    public function getSelectedItems() : Array {
      return selectedChildren.source;
    }


    //************************************************
    //  IClipboardClient impl
    //************************************************

    /**
     *
     * @return
     *
     */
    public function get clipboardEnabled() : Boolean {
      return _clipboardEnabled;
    }


    /**
     *
     * @param value
     *
     */
    public function set clipboardEnabled(value : Boolean) : void {
      if (value != _clipboardEnabled) {
        _clipboardEnabled = value;
      }
    }


    /**
     *
     * @return
     *
     */
    public function copy() : Array {
      return getSelectedItems();
    }


    /**
     *
     * @return
     *
     */
    public function cut() : Array {
      unselectAllChildren();
      return getSelectedItems();
    }


    /**
     *
     * @param items
     *
     */
    public function paste(items : Array) : void {
      for each (var item : *in items) {
        if (item is DisplayObject) {
          addChild(item);
        }
      }
    }


    /**
     *
     * @return
     *
     */
    public function get indexedChildren() : Array {
      return _indexedChildren;
    }


    /**
     *
     * @param value
     *
     */
    public function set verticalGap(value : int) : void {
      if (value != _verticalGap) {
        _verticalGap = value;
        if (layoutObject) {
          AbstractLayout(layoutObject).verticalGap = _verticalGap;
          invalidateDisplayList();
        }
      }
    }


    /**
     *
     * @param child
     * @return
     *
     */
    override public function addChild(child : DisplayObject) : DisplayObject {
      _indexedChildren.push(child);
      child.addEventListener(MouseEvent.CLICK, onChildClick);
      child.addEventListener(MouseEvent.MOUSE_MOVE, onChildMove);
      return super.addChildAt(child, numChildren);
    }


    /**
     *
     * @param child
     * @return
     *
     */
    override public function removeChild(child : DisplayObject) : DisplayObject {
      if (_indexedChildren.indexOf(child) > -1) {
        _indexedChildren.splice(_indexedChildren.indexOf(child), 1);
        child.removeEventListener(MouseEvent.CLICK, onChildClick);
        child.removeEventListener(MouseEvent.MOUSE_MOVE, onChildMove);
      }
      return super.removeChild(child);
    }


    /**
     *
     *
     */
    override public function removeAllChildren() : void {
      for each (var child : DisplayObject in getChildren()) {
        child.removeEventListener(MouseEvent.CLICK, onChildClick);
        child.removeEventListener(MouseEvent.MOUSE_MOVE, onChildMove);
      }
      super.removeAllChildren();
      _indexedChildren = [];
    }


    /**
     *
     * @param child
     * @param index
     * @return
     *
     */
    override public function addChildAt(child : DisplayObject, index : int) : DisplayObject {
      var firstPart : Array = _indexedChildren.splice(0, index);
      firstPart.push(child);
      child.addEventListener(MouseEvent.CLICK, onChildClick);
      child.addEventListener(MouseEvent.MOUSE_MOVE, onChildMove);
      _indexedChildren = firstPart.concat(_indexedChildren);
      return super.addChildAt(child, index);
    }


    /**
     *
     * @param index
     * @return
     *
     */
    override public function removeChildAt(index : int) : DisplayObject {
      var child : DisplayObject = DisplayObject(_indexedChildren[index]);
      _indexedChildren.splice(index, 1);
      return super.removeChild(child);
    }


    /**
     *
     * @return
     *
     */
    public function get verticalGap() : int {
      return _verticalGap;
    }


    /**
     *
     * @param value
     *
     */
    public function set horizontalGap(value : int) : void {
      if (value != _horizontalGap) {
        _horizontalGap = value;
        if (layoutObject) {
          AbstractLayout(layoutObject).horizontalGap = horizontalGap;
          invalidateDisplayList();
        }
      }
    }


    /**
     *
     * @return
     *
     */
    public function get horizontalGap() : int {
      return _horizontalGap;
    }


    /**
     *
     * @param value
     *
     */
    public function set layoutClass(value : ClassFactory) : void {
      if (value.generator != _layoutClass) {
        AbstractLayout(layoutObject).unregisterListeners();

        layoutObject = null;
        layoutObject = value.newInstance() as AbstractLayout;
        layoutObject.target = this;

        if (itemWidth != 0) {
          AbstractLayout(layoutObject).itemWidth = itemWidth;
        }
        if (itemHeight != 0) {
          AbstractLayout(layoutObject).itemHeight = itemHeight;
        }

        AbstractLayout(layoutObject).verticalGap = verticalGap;
        AbstractLayout(layoutObject).horizontalGap = horizontalGap;

        dispatchEvent(new Event("layoutChanged"));

        invalidateDisplayList();
      }
    }


    [Bindable("layoutChanged")]
    /**
     *
     * @return
     *
     */
    public function get layoutClass() : ClassFactory {
      return new ClassFactory(_layoutClass);
    }


    /**
     *
     * @param value
     *
     */
    override public function set layout(value : String) : void {
    }


    protected function isChildSelected(child : DisplayObject) : Boolean {
      if (selectedChildren) {
        for each (var item : DisplayObject in selectedChildren) {
          if (item == child) {
            return true;
          }
        }
      }
      return false;
    }


    /**
     *
     * @param child
     *
     */
    protected function selectChild(child : DisplayObject) : void {
      if (!isChildSelected(child)) {
        var glow : GlowFilter = new GlowFilter(selectionColor, 1, 2, 2, 50, 2, true);
        var newFilters : Array = [glow];
        newFilters = newFilters.concat(filters);
        child.filters = newFilters;
      }
    }


    /**
     *
     * @param child
     *
     */
    protected function unselectChild(child : DisplayObject) : void {
      if (isChildSelected(child)) {
        if (child.filters && child.filters.length > 0) {
          var newFilters : Array = child.filters;
          newFilters.shift();
          child.filters = newFilters;
        }
      }
    }


    /**
     *
     *
     */
    private function unselectAllChildren() : void {
      for each (var child : DisplayObject in selectedChildren) {
        unselectChild(child);
      }
    }


    /**
     *
     * @param child
     *
     */
    private function removeFromSelected(child : DisplayObject) : void {
      var itemIndex : uint = selectedChildren.getItemIndex(child);
      if (itemIndex >= 0) {
        selectedChildren.removeItemAt(itemIndex);
      }
    }


    /**
     *
     * @param event
     *
     */
    protected function onDragEnter(event : DragEvent) : void {
      if (dropEnabled) {
        if (event.dragSource.hasFormat('child')) {
          DragManager.acceptDragDrop(this);
        }
      }
    }


    /**
     *
     * @param event
     *
     */
    protected function onDragDrop(event : DragEvent) : void {
      var child : UIComponent = UIComponent(event.dragSource.dataForFormat('child'));
      addChild(child);
    }


    /**
     *
     * @param event
     *
     */
    protected function onKeyDown(event : KeyboardEvent) : void {
      if (deleteEnabled) {
        // DELETE KEY
        if (event.keyCode == 46) {
          var selectedItems : Array = getSelectedItems()
          if (selectedItems.length > 0) {
            for each (var item : DisplayObject in selectedItems) {
              unselectChild(item);
              removeChild(item);
            }
          }
        }
      }
    }


    /**
     *
     * @param event
     *
     */
    protected function onChildMove(event : MouseEvent) : void {
      if (dragEnabled) {
        var dragInitiator : UIComponent = UIComponent(event.currentTarget);
        var ds : DragSource = new DragSource();
        ds.addData(dragInitiator, 'child');
        DragManager.doDrag(dragInitiator, ds, event);
      }
    }



    /**
     *
     *
     */
    protected function onChildClick(event : MouseEvent) : void {
      var child : UIComponent = UIComponent(event.currentTarget);
      if (selectable) {
        if (isChildSelected(child)) {
          if (event.ctrlKey) {
            unselectChild(child);
            removeFromSelected(child);
          } else {
            unselectAllChildren();
            selectedChildren.removeAll();
          }
        } else {
          if (!allowMultipleSelection) {
            unselectAllChildren();
            selectedChildren.removeAll();
          } else {
            if (!event.ctrlKey) {
              unselectAllChildren();
              selectedChildren.removeAll();
            }
          }
          selectChild(child);
          selectedChildren.addItem(child);
        }
      }
    }

  }
}