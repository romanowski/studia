package pl.flexable.harmonix.layout {
  import caurina.transitions.Tweener;

  import flash.display.DisplayObject;
  import flash.events.MouseEvent;

  import mx.core.Container;
  import mx.core.ScrollPolicy;
  import mx.core.UIComponent;
  import mx.core.mx_internal;
  import mx.events.MoveEvent;
  import mx.events.ScrollEvent;

  import pl.flexable.harmonix.container.LayoutContainer;

  use namespace mx_internal;


  /**
   *
   * @author Mateusz
   *
   */
  public class CoverFlowLayout extends AbstractLayout {

    /**
     *
     */
    private var oldScrollPosition : int = 0;

    /**
     *
     */
    private var oldIndex : int = 0;

    /**
     *
     */
    private var _animationDuration : Number = 0.1;

    /**
     *
     */
    private var _selectedIndex : int = 0;

    /**
     *
     */
    private var _rollOverScaleFactor : Number = 1.5;


    /**
     *
     * @return
     *
     */
    public function get animationDuration() : Number {
      return _animationDuration;
    }


    /**
     *
     * @param value
     *
     */
    public function set animationDuration(value : Number) : void {
      if (value != _animationDuration) {
        _animationDuration = value;
      }
    }


    /**
     *
     * @param value
     *
     */
    public function set selectedIndex(value : int) : void {
      if (value != _selectedIndex) {
        _selectedIndex = value;
      }
    }


    /**
     *
     * @return
     *
     */
    private function getWidthPerItem() : int {
      return (target.maxHorizontalScrollPosition) / (target.numChildren - 1);
    }


    /**
     *
     * @return
     *
     */
    public function get selectedIndex() : int {
      return _selectedIndex;
    }


    /**
     *
     *
     */
    public function CoverFlowLayout() {
      super();
    }


    /**
     *
     * @param item
     * @return
     *
     */
    private function getIndexFromItem(item : UIComponent) : int {
      var children : Array = target.getChildren();
      for (var i : int = 0; i < children.length; i++) {
        if (item == children[i]) {
          return i;
        }
      }
      return -1;
    }


    /**
     *
     * @param event
     *
     */
    override protected function child_rollOverHandler(event : MouseEvent) : void {
      invalidateLayoutFlag = true;
    }


    /**
     *
     * @param event
     *
     */
    override protected function child_rollOutHandler(event : MouseEvent) : void {
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
     * @param event
     *
     */
    override protected function child_clickHandler(event : MouseEvent) : void {
      var index : int = gett(UIComponent(event.currentTarget));
      if (index != -1) {
        LayoutContainer(target).indexedChildren
        target.horizontalScrollPosition = getScrollPositionForIndex(index);
        selectedIndex = index;
        invalidateLayoutFlag = true;
      }
    }


    private function gett(child : UIComponent) : int {
      var children : Array = LayoutContainer(target).indexedChildren;
      for (var i : int = 0; i < children.length; i++) {
        if (child == children[i]) {
          return i;
        }
      }
      return -1;
    }


    /**
     *
     * @param value
     *
     */
    override public function set target(value : Container) : void {
      if (value != target) {
        if (target) {
          target.removeEventListener(ScrollEvent.SCROLL, onScroll);
        }

        super.target = value;

        target.horizontalScrollPolicy = ScrollPolicy.ON;
        target.verticalScrollPolicy = ScrollPolicy.OFF;

        target.addEventListener(ScrollEvent.SCROLL, onScroll);
      }
    }


    private function getScrollPositionForIndex(index : int) : Number {
      var widthPerItem : Number = getWidthPerItem();
      var newPosition : Number = widthPerItem * index;
      return newPosition;
    }


    /**
     *
     * @param event
     *
     */
    private function onScroll(event : ScrollEvent) : void {
      if (oldScrollPosition == event.position) {
        invalidateLayoutFlag = false;
      } else {

        oldScrollPosition = event.position;
        var widthPerItem : Number = getWidthPerItem();
        var currentIndex : int = int((event.position + (widthPerItem / 2)) / widthPerItem);
        if (currentIndex != oldIndex) {
          oldIndex = selectedIndex;
          selectedIndex = currentIndex;
          invalidateLayoutFlag = true;
        } else {
          invalidateLayoutFlag = false;
        }
      }
    }




    /**
     *
     * @param unscaledWidth
     * @param unscaledHeight
     *
     */
    override public function updateDisplayList(unscaledWidth : Number, unscaledHeight : Number) : void {
      if (invalidateLayoutFlag) {
        super.updateDisplayList(unscaledWidth, unscaledHeight);

        var scale : Number = _rollOverScaleFactor;
        var scaleDelta : Number = (scale - 1);
        var deltaWidth : Number = scaleDelta * maximumItemWidth / 2;
        var deltaWidthPerItem : Number = scaleDelta * maximumItemWidth / target.numChildren;

        var layoutMetrics : LayoutMetrics = new LayoutMetrics(target);
        var startX : int = layoutMetrics.left;
        var children : Array = LayoutContainer(target).indexedChildren;
        for (var i : int = 0; i < children.length; i++) {
          var child : DisplayObject = DisplayObject(children[i]);
          var childUI : UIComponent = UIComponent(children[i]);

          var newY : int = int((target.height - layoutMetrics.top - layoutMetrics.bottom - maximumItemHeight) / 2);
          var newX : int = startX + i * (horizontalGap + maximumItemWidth);
          var scaleX : Number = 1;
          var scaleY : Number = 1;
          var time : Number = animationDuration;
          child.scaleX = 1;
          child.scaleY = 1;
          if (i == selectedIndex) {
            newX -= deltaWidthPerItem * i;
            newY -= deltaWidth;
            scaleX = scaleY = 1.5;
            time = animationDuration;
            invalidateLayoutFlag = false;
            target.setChildIndex(child, target.numChildren - 1);
          }
          Tweener.addTween(child, {x: newX, y: newY, scaleX: scaleX, scaleY: scaleY, transition: "linear", time: animationDuration});
        }
      }
    }
  }
}