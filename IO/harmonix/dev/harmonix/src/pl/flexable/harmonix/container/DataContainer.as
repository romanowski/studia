package pl.flexable.harmonix.container {
  import flash.display.DisplayObject;
  import flash.events.Event;
  import flash.events.KeyboardEvent;
  import flash.events.MouseEvent;

  import mx.collections.ArrayCollection;
  import mx.collections.IList;
  import mx.collections.IViewCursor;
  import mx.collections.ListCollectionView;
  import mx.collections.XMLListCollection;
  import mx.core.DragSource;
  import mx.core.IDataRenderer;
  import mx.core.IFactory;
  import mx.core.UIComponent;
  import mx.events.CollectionEvent;
  import mx.events.CollectionEventKind;
  import mx.events.DragEvent;
  import mx.managers.DragManager;


  /**
   *
   * @author Mateusz
   *
   */
  public class DataContainer extends LayoutContainer {

    /**
     *
     */
    private var _dataProvider : Object;

    /**
     *
     */
    private var _itemRenderer : IFactory;

    /**
     *
     */
    private var collection : ListCollectionView;

    /**
     *
     */
    private var iterator : IViewCursor;

    /**
     *
     */
    private var collectionIterator : IViewCursor;


    /**
     *
     *
     */
    public function DataContainer() {
      super();
    }


    //************************************************
    //  ISelectableContainer impl
    //************************************************
    /**
     *
     * @return
     *
     */
    override public function getSelectedItems() : Array {
      var items : Array = [];
      for each (var item : IDataRenderer in selectedChildren) {
        items.push(item.data);
      }
      return items;
    }


    //************************************************
    //  IClipboardClient impl
    //************************************************


    /**
     *
     * @param items
     *
     */
    override public function paste(items : Array) : void {
      for each (var item : *in items) {
        if (!(item is DisplayObject)) {
          collection.addItem(item);
        }
      }
      invalidateDisplayList();
    }


    /**
     *
     * @param value
     *
     */
    public function set itemRenderer(value : IFactory) : void {
      if (_itemRenderer != value) {
        _itemRenderer = value;

        rebuildChildren();

        dispatchEvent(new Event("itemRendererChanged"));
      }
    }


    [Bindable("itemRendererChanged")]
    /**
     *
     * @return
     *
     */
    public function get itemRenderer() : IFactory {
      return _itemRenderer;
    }


    /**
     *  @private
     */
    public function set dataProvider(value : Object) : void {
      if (collection) {
        collection.removeEventListener(CollectionEvent.COLLECTION_CHANGE, collectionChangeHandler);
      }

      if (value is Array) {
        collection = new ArrayCollection(value as Array);
      } else if (value is ArrayCollection) {
        collection = value as ListCollectionView;
      } else if (value is IList) {
        collection = new ListCollectionView(IList(value));
      } else if (value is XMLList) {
        collection = new XMLListCollection(value as XMLList);
      } else if (value is XML) {
        var xl : XMLList = new XMLList();
        xl += value;
        collection = new XMLListCollection(xl);
      } else {
        var tmp : Array = [];
        if (value != null)
          tmp.push(value);
        collection = new ArrayCollection(tmp);
      }

      iterator = collection.createCursor();
      collectionIterator = collection.createCursor();

      collection.addEventListener(CollectionEvent.COLLECTION_CHANGE, collectionChangeHandler, false, 0, true);

      var event : CollectionEvent = new CollectionEvent(CollectionEvent.COLLECTION_CHANGE);
      event.kind = CollectionEventKind.RESET;
      collectionChangeHandler(event);
      dispatchEvent(event);

      rebuildChildren();
    }


    [Bindable("collectionChange")]
    /**
     *
     * @return
     *
     */
    public function get dataProvider() : Object {
      return _dataProvider;
    }


    /**
     *
     * @param item
     *
     */
    public function removeDataChild(item : Object) : void {
      var index : int = collection.getItemIndex(item);
      if (index > -1) {
        collection.removeItemAt(index);
      }
    }


    /**
     *
     * @param event
     *
     */
    private function collectionChangeHandler(event : CollectionEvent) : void {
      switch (event.kind) {
        case CollectionEventKind.ADD:
          adjustAfterAdd(event);
          break;
        case CollectionEventKind.REMOVE:
          adjustAfterRemove(event);
          break;
        default:
          rebuildChildren();
          break;

      }
    }


    /**
     *
     * @param event
     *
     */
    private function adjustAfterRemove(event : CollectionEvent) : void {
      var renderer : DisplayObject = getRendererForItem(event.items[0]);
      if (renderer) {
        removeChild(renderer);
      }
    }


    /**
     *
     * @param event
     *
     */
    private function adjustAfterAdd(event : CollectionEvent) : void {
      var renderer : IDataRenderer = IDataRenderer(itemRenderer.newInstance());
      renderer.data = event.items[0];
      addChildAt(DisplayObject(renderer), event.location);
    }


    /**
     *
     * @param item
     * @return
     *
     */
    private function getRendererForItem(item : Object) : DisplayObject {
      var children : Array = getChildren();
      for each (var renderer : IDataRenderer in children) {
        if (renderer.data == item) {
          return renderer as DisplayObject;
        }
      }
      return null;
    }


    /**
     *
     * @param event
     *
     */
    override protected function onDragEnter(event : DragEvent) : void {
      if (dropEnabled) {
        if (event.dragSource.hasFormat('data')) {
          DragManager.acceptDragDrop(this);
        }
      }
    }


    /**
     *
     * @param event
     *
     */
    override protected function onDragDrop(event : DragEvent) : void {
      var item : Object = Object(event.dragSource.dataForFormat('data'));
      collection.addItem(item);
      invalidateDisplayList();
    }


    /**
     *
     * @param event
     *
     */
    override protected function onKeyDown(event : KeyboardEvent) : void {
      if (deleteEnabled) {
        // DELETE KEY
        if (event.keyCode == 46) {
          var selectedItems : Array = getSelectedItems()
          if (selectedItems.length > 0) {
            for each (var item : Object in selectedItems) {
              unselectChild(getRendererForItem(item));
              removeDataChild(item);
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
    override protected function onChildMove(event : MouseEvent) : void {
      if (dragEnabled) {
        var dragInitiator : IDataRenderer = IDataRenderer(event.currentTarget);
        var ds : DragSource = new DragSource();
        ds.addData(dragInitiator.data, 'data');
        DragManager.doDrag(UIComponent(dragInitiator), ds, event);
      }
    }


    /**
     *
     *
     */
    private function rebuildChildren() : void {
      removeAllChildren();
      for each (var item : Object in collection) {
        var renderer : IDataRenderer = IDataRenderer(itemRenderer.newInstance());
        renderer.data = item;
        addChild(DisplayObject(renderer));
      }
    }

  }
}