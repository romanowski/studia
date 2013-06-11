package pl.flexable.harmonix.events {
  import flash.events.Event;


  /**
   *
   * @author Mateusz
   *
   */
  public class ClipboardManagerEvent extends Event {

    /**
     *
     */
    public static const ADDED_TO_CLIPBOARD : String = "addedToClipboard";

    /**
     *
     */
    public static const CLIPBOARD_CLEARED : String = "clipboardCleared";


    /**
     *
     */
    public static const CHANGED : String = "clipboardChanged";


    /**
     *
     */
    public var data : Array;


    /**
     *
     * @param type
     * @param bubbles
     * @param cancelable
     *
     */
    public function ClipboardManagerEvent(type : String, data : Array = null, bubbles : Boolean = false, cancelable : Boolean = false) {
      super(type, bubbles, cancelable);

      this.data = data;
    }


    /**
     *
     * @return
     *
     */
    override public function clone() : Event {
      return new ClipboardManagerEvent(type, data, bubbles, cancelable);
    }
  }
}