package pl.flexable.harmonix.clipboard {

  import flash.events.Event;
  import flash.events.EventDispatcher;

  import mx.collections.ArrayCollection;
  import mx.events.CollectionEvent;

  import pl.flexable.harmonix.errors.HarmonixError;
  import pl.flexable.harmonix.events.ClipboardManagerEvent;


  public class ClipboardManager extends EventDispatcher {

    private static var instance : ClipboardManager;

    private var _clipboard : ArrayCollection = new ArrayCollection();


    //*********************************************
    //  Singleton class
    //*********************************************   

    /**
     *
     * @param privateClass
     *
     */
    public function ClipboardManager() {
      super(this);

      if (instance) {
        throw new HarmonixError(HarmonixError.SINGLETON_CLASS_ERROR);
      }
    }


    /**
     *
     * @return
     *
     */
    public static function getInstance() : ClipboardManager {
      if (!instance) {
        instance = new ClipboardManager();
      }
      return instance;
    }


    /**
     *
     * @param value
     *
     */
    public function set clipboard(value : ArrayCollection) : void {
      if (_clipboard != value) {
        if (_clipboard) {
          _clipboard.removeEventListener(CollectionEvent.COLLECTION_CHANGE, onCollectionChange);
        }
        _clipboard = value;
        if (!_clipboard) {
          dispatchEvent(new ClipboardManagerEvent(ClipboardManagerEvent.CLIPBOARD_CLEARED));
        } else {
          _clipboard.addEventListener(CollectionEvent.COLLECTION_CHANGE, onCollectionChange);
        }
        dispatchEvent(new ClipboardManagerEvent(ClipboardManagerEvent.CHANGED, value.source));
        dispatchEvent(new Event("clipboardChanged"));
      }
    }


    [Bindable("clipboardChanged")]
    /**
     *
     * @return
     *
     */
    public function get clipboard() : ArrayCollection {
      return _clipboard;
    }


    /**
     *
     * @param event
     *
     */
    private function onCollectionChange(event : CollectionEvent) : void {
      dispatchEvent(new Event("clipboardChanged"));
    }


    /**
     *
     * @param items
     *
     */
    public function addToClipboard(items : Array) : void {
      if (items.length > 0) {
        clipboard = new ArrayCollection(items);
      }
    }


    /**
     *
     *
     */
    public function clearClipboard() : void {
      if (clipboard) {
        clipboard.removeAll();
        dispatchEvent(new ClipboardManagerEvent(ClipboardManagerEvent.CLIPBOARD_CLEARED));
      }
    }

  }
}
