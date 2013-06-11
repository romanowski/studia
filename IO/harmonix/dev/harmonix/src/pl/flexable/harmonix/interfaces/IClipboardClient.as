package pl.flexable.harmonix.interfaces {
  import flash.events.IEventDispatcher;



  /**
   *
   * @author Mateusz
   *
   */
  public interface IClipboardClient extends ISelectableContainer, IEventDispatcher {


    /**
     *
     * @return
     *
     */
    function get clipboardEnabled() : Boolean;

    /**
     *
     * @param value
     *
     */
    function set clipboardEnabled(value : Boolean) : void;

    /**
     *
     *
     */
    function copy() : Array;

    /**
     *
     *
     */
    function cut() : Array;

    /**
     *
     *
     */
    function paste(items : Array) : void;
  }
}