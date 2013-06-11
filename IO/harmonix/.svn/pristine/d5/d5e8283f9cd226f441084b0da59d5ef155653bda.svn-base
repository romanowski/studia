package pl.flexable.harmonix.layout {
  import mx.core.UIComponent;


  /**
   *
   * @author Mateusz
   *
   */
  public class LayoutMetrics {

    /**
     *
     */
    public var left : Number = 0;

    /**
     *
     */
    public var right : Number = 0;

    /**
     *
     */
    public var top : Number = 0;

    /**
     *
     */
    public var bottom : Number = 0;


    /**
     *
     * @param target
     *
     */
    public function LayoutMetrics(target : UIComponent = null) {
      if (target) {
        left = target.getStyle("paddingLeft");
        right = target.getStyle("paddingRight");
        bottom = target.getStyle("paddingBottom");
        top = target.getStyle("paddingTop");
      }
    }
  }
}