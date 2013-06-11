package pl.flexable.harmonix.layout {
  import caurina.transitions.Tweener;

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
  public class VerticalSeriesLayout extends AbstractLayout {


    /**
     *
     *
     */
    public function VerticalSeriesLayout() {
      super();
    }


    /**
     *
     * @param event
     *
     */
    override protected function child_rollOverHandler(event : MouseEvent) : void {
      invalidateLayoutFlag = false;
      var startY : Number = Point(childPositions[event.currentTarget]).y;
      Tweener.addTween(event.currentTarget, {y: startY - 20, transition: "linear", time: SELECTION_ANIMATION_DURATION});
    }


    /**
     *
     * @param event
     *
     */
    override protected function child_rollOutHandler(event : MouseEvent) : void {
      var startY : Number = Point(childPositions[event.currentTarget]).y;
      Tweener.addTween(event.currentTarget, {y: startY, transition: "linear", time: SELECTION_ANIMATION_DURATION});
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
      target.horizontalScrollPolicy = ScrollPolicy.AUTO;
      target.verticalScrollPolicy = ScrollPolicy.OFF;
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
        var maxRows : int = int((unscaledHeight - layoutMetrics.top - layoutMetrics.bottom - target.viewMetrics.bottom) / verticalGap);

        childPositions = new Dictionary();
        for (var i : int = 0; i < target.numChildren; i++) {

          var row : int = i % maxRows;
          var col : int = int(i / maxRows);

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
          var newY : int = layoutMetrics.top + row * verticalGap;
          var newX : int = layoutMetrics.left + col * (horizontalGap + maximumItemWidth);
          childPositions[child] = new Point(newX, newY);
          child.move(newX, newY);
        }
      }
    }
  }
}