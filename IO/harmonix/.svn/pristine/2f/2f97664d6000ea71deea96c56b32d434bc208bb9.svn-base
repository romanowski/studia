package pl.flexable.harmonix.layout {
  import flash.display.DisplayObject;
  import flash.events.MouseEvent;
  import flash.utils.Dictionary;

  import mx.core.Container;
  import mx.core.UIComponent;
  import mx.events.ChildExistenceChangedEvent;
  import mx.events.MoveEvent;

  import pl.flexable.harmonix.errors.HarmonixError;
  import mx.containers.utilityClasses.Layout;


  /**
   *
   * @author Mateusz
   *
   */
  public class AbstractLayout extends mx.containers.utilityClasses.Layout {


    /**
     *
     */
    protected const SELECTION_ANIMATION_DURATION : Number = 0.2;

    /**
     *
     */
    protected var invalidateLayoutFlag : Boolean = true;

    /**
     *
     */
    protected var invalidateItemsWidthFlag : Boolean = true;

    /**
     *
     */
    protected var invalidateItemsHeightFlag : Boolean = true;

    /**
     *
     */
    private var _maximumItemWidth : int;

    /**
     *
     */
    private var _maximumItemHeight : int;

    /**
     *
     */
    private var _verticalGap : int = 20;

    /**
     *
     */
    private var _horizontalGap : int = 20;

    /**
     *
     */
    private var _itemWidth : Number = 0;

    /**
     *
     */
    private var _itemHeight : Number = 0;

    /**
     *
     */
    protected var childPositions : Dictionary = new Dictionary();


    /**
     *
     *
     */
    public function AbstractLayout() {
      super();
    }


    /**
     *
     * @param value
     *
     */
    public function set itemWidth(value : Number) : void {
      if (value != _itemWidth) {
        _itemWidth = value;
        invalidateItemsWidthFlag = true;
        invalidateItemsHeightFlag = true;
        invalidateLayoutFlag = true;
        target.invalidateDisplayList();
      }
    }


    /**
     *
     * @return
     *
     */
    public function get itemWidth() : Number {
      return _itemWidth;
    }


    /**
     *
     * @param value
     *
     */
    public function set itemHeight(value : Number) : void {
      if (value != _itemHeight) {
        _itemHeight = value;
        invalidateItemsHeightFlag = true;
        invalidateItemsWidthFlag = true;
        invalidateLayoutFlag = true;
        target.invalidateDisplayList();
      }
    }


    /**
     *
     * @return
     *
     */
    public function get itemHeight() : Number {
      return _itemHeight;
    }


    /**
     *
     * @param value
     *
     */
    public function set verticalGap(value : int) : void {
      if (value != _verticalGap) {
        _verticalGap = value;
      }
    }


    /**
     *
     * @return
     *
     */
    public function get verticalGap() : int {
      return _verticalGap;
    }


    /**
     *
     * @param value
     *
     */
    public function set horizontalGap(value : int) : void {
      if (value != _horizontalGap) {
        _horizontalGap = value;
      }
    }


    /**
     *
     * @return
     *
     */
    public function get horizontalGap() : int {
      return _horizontalGap;
    }




    /**
     *
     * @return
     *
     */
    public function get maximumItemHeight() : int {
      if (itemHeight != 0) {
        return itemHeight;
      }
      if (target && (invalidateItemsHeightFlag || isNaN(_maximumItemHeight))) {
        var children : Array = target.getChildren();
        var newMaxItemHeight : int = 0;
        for each (var child : UIComponent in children) {
          if (child.height > newMaxItemHeight) {
            newMaxItemHeight = child.height / child.scaleY;
          }
        }
        _maximumItemHeight = newMaxItemHeight;
        invalidateItemsHeightFlag = false;
      }
      return _maximumItemHeight
    }


    /**
     *
     * @return
     *
     */
    public function get maximumItemWidth() : int {
      if (itemWidth != 0) {
        return itemWidth;
      }
      if (target && (invalidateItemsWidthFlag || isNaN(_maximumItemWidth))) {
        var children : Array = target.getChildren();
        var newMaxItemWidth : int = 0;
        for each (var child : UIComponent in children) {
          if (child.width > newMaxItemWidth) {
            newMaxItemWidth = child.width / child.scaleX;
          }
        }
        _maximumItemWidth = newMaxItemWidth;
        invalidateItemsWidthFlag = false;
      }
      return _maximumItemWidth;
    }


    /**
     *
     *
     */
    public function unregisterListeners() : void {
      target.removeEventListener(ChildExistenceChangedEvent.CHILD_ADD, target_childAddHandler);
      target.removeEventListener(ChildExistenceChangedEvent.CHILD_REMOVE, target_childRemoveHandler);

      for each (var child : UIComponent in target.getChildren()) {
        child.removeEventListener(MoveEvent.MOVE, child_moveHandler);
        child.removeEventListener(MouseEvent.ROLL_OVER, child_rollOverHandler);
        child.removeEventListener(MouseEvent.ROLL_OUT, child_rollOutHandler);
      }
    }


    /**
     *
     * @param value
     *
     */
    override public function set target(value : Container) : void {
      if (value != target) {
        var i : int;
        var n : int;

        if (target) {

          target.removeEventListener(ChildExistenceChangedEvent.CHILD_ADD, target_childAddHandler);
          target.removeEventListener(ChildExistenceChangedEvent.CHILD_REMOVE, target_childRemoveHandler);

          n = target.numChildren;
          for (i = 0; i < n; i++) {
            UIComponent(target.getChildAt(i)).removeEventListener(MouseEvent.CLICK, child_clickHandler);
            UIComponent(target.getChildAt(i)).removeEventListener(MoveEvent.MOVE, child_moveHandler);
            UIComponent(target.getChildAt(i)).removeEventListener(MouseEvent.ROLL_OVER, child_rollOverHandler);
            UIComponent(target.getChildAt(i)).removeEventListener(MouseEvent.ROLL_OUT, child_rollOutHandler);
          }
        }

        if (value) {
          value.addEventListener(ChildExistenceChangedEvent.CHILD_ADD, target_childAddHandler);
          value.addEventListener(ChildExistenceChangedEvent.CHILD_REMOVE, target_childRemoveHandler);

          n = value.numChildren;
          for (i = 0; i < n; i++) {
            UIComponent(value.getChildAt(i)).addEventListener(MouseEvent.CLICK, child_clickHandler);
            UIComponent(value.getChildAt(i)).addEventListener(MoveEvent.MOVE, child_moveHandler);
            UIComponent(value.getChildAt(i)).addEventListener(MouseEvent.ROLL_OVER, child_rollOverHandler);
            UIComponent(value.getChildAt(i)).addEventListener(MouseEvent.ROLL_OUT, child_rollOutHandler);
          }
        }

        super.target = value;
      }
    }


    /**
     *
     * @param event
     *
     */
    protected function child_rollOverHandler(event : MouseEvent) : void {
      throw new HarmonixError(HarmonixError.ABSTRACT_METHOD_ERROR);
    }


    /**
     *
     * @param event
     *
     */
    protected function child_clickHandler(event : MouseEvent) : void {
      //
    }


    /**
     *
     * @param event
     *
     */
    protected function child_rollOutHandler(event : MouseEvent) : void {
      throw new HarmonixError(HarmonixError.ABSTRACT_METHOD_ERROR);
    }


    /**
     *
     * @param event
     *
     */
    protected function child_moveHandler(event : MoveEvent) : void {
      throw new HarmonixError(HarmonixError.ABSTRACT_METHOD_ERROR);
    }


    /**
     *
     * @param event
     *
     */
    protected function target_childAddHandler(event : ChildExistenceChangedEvent) : void {
      DisplayObject(event.relatedObject).addEventListener(MouseEvent.ROLL_OVER, child_rollOverHandler);
      DisplayObject(event.relatedObject).addEventListener(MouseEvent.ROLL_OUT, child_rollOutHandler);
      invalidateItemsHeightFlag = true;
      invalidateItemsWidthFlag = true;
      invalidateLayoutFlag = true;
    }


    /**
     *
     * @param event
     *
     */
    protected function target_childRemoveHandler(event : ChildExistenceChangedEvent) : void {
      DisplayObject(event.relatedObject).removeEventListener(MouseEvent.ROLL_OVER, child_rollOverHandler);
      DisplayObject(event.relatedObject).removeEventListener(MouseEvent.ROLL_OUT, child_rollOutHandler);
      invalidateItemsHeightFlag = true;
      invalidateItemsWidthFlag = true;
      invalidateLayoutFlag = true;
    }
  }
}