package pl.flexable.harmonix.layout {
  import flash.display.DisplayObject;
  import flash.events.MouseEvent;
  import flash.geom.Point;
  import flash.utils.Dictionary;

  import mx.core.Container;
  import mx.core.IUIComponent;
  import mx.core.ScrollPolicy;
  import mx.events.MoveEvent;


  /**
   *
   * @author Mateusz
   *
   */
  public class HorizontalSeriesLayout extends AbstractLayout {


    /**
     *
     *
     */
    public function HorizontalSeriesLayout() {
      super();
    }


    /**
     *
     * @param event
     *
     */
    override protected function child_rollOverHandler(event : MouseEvent) : void {
      invalidateLayoutFlag = false;
      DisplayObject(event.currentTarget).x -= 20;
    }


    /**
     *
     * @param event
     *
     */
    override protected function child_rollOutHandler(event : MouseEvent) : void {
      DisplayObject(event.currentTarget).x += 20;
      invalidateLayoutFlag = true;
    }


    /**
     *
     * @param event
     *
     */
    override protected function child_moveHandler(event : MoveEvent) : void {
      //
    }


    /**
     *
     * @param value
     *
     */
    override public function set target(value : Container) : void {
      super.target = value;
      target.verticalScrollPolicy = ScrollPolicy.AUTO;
      target.horizontalScrollPolicy = ScrollPolicy.OFF;
    }


    /**
     *
     * @param unscaledWidth
     * @param unscaledHeight
     *
     */
    override public function updateDisplayList(unscaledWidth : Number, unscaledHeight : Number) : void {
      super.updateDisplayList(unscaledWidth, unscaledHeight);

      if (invalidateLayoutFlag) {
        var layoutMetrics : LayoutMetrics = new LayoutMetrics(target);
        var maxCols : int = int((unscaledWidth - layoutMetrics.right - target.viewMetrics.right - target.viewMetrics.right) / horizontalGap);

        childPositions = new Dictionary();
        for (var i : int = 0; i < target.numChildren; i++) {

          var row : int = int(i / maxCols);
          var col : int = i % maxCols;

          var child : IUIComponent = IUIComponent(target.getChildAt(i));
          if (itemWidth != 0) {
            child.width = itemWidth;
          }
          if (itemHeight != 0) {
            child.height = itemHeight;
          }
          child.scaleX = 1;
          child.scaleY = 1;
          //check if positioned child is in visible area
          var newY : int = layoutMetrics.top + row * (verticalGap + maximumItemHeight);
          var newX : int = layoutMetrics.left + col * horizontalGap;
          childPositions[child] = new Point(newX, newY);
          child.move(newX, newY);
        }
      }
    }

  }
}