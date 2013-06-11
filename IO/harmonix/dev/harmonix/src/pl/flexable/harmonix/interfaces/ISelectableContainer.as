package pl.flexable.harmonix.interfaces {


  /**
   *
   * @author Mateusz
   *
   */
  public interface ISelectableContainer {

    /**
     *
     * @return
     *
     */
    function get selectable() : Boolean;

    /**
     *
     * @param value
     * @return
     *
     */
    function set selectable(value : Boolean) : void;

    /**
     *
     * @return
     *
     */
    function get allowMultipleSelection() : Boolean;

    /**
     *
     * @param value
     * @return
     *
     */
    function set allowMultipleSelection(value : Boolean) : void;

    /**
     *
     * @return
     *
     */
    function getSelectedItems() : Array;

  }
}