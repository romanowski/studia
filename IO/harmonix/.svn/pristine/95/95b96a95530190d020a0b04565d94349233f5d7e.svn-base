package pl.flexable.harmonix.events {
  import flash.events.Event;


  /**
   *
   * @author Mateusz
   *
   */
  public class ClipboardClientEvent extends Event {

    /**
     *
     */
    public static const CUT : String = "cut";

    /**
     *
     */
    public static const COPY : String = "copy";

    /**
     *
     */
    public static const PASTE : String = "paste";


    /**
     *
     */
    public var items : Array;


    /**
     *
     * @param type
     * @param bubbles
     * @param cancelable
     *
     */
    public function ClipboardClientEvent(type : String, items : Array = null, bubbles : Boolean = false, cancelable : Boolean = false) {
      super(type, bubbles, cancelable);

      this.items = items;
    }


    /**
     *
     * @return
     *
     */
    override public function clone() : Event {
      return new ClipboardClientEvent(type, items, bubbles, cancelable);
    }
  }
}