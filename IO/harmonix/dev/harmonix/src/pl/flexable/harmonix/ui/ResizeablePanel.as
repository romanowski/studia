package pl.flexable.harmonix.ui {
  import flash.events.Event;
  import flash.events.MouseEvent;
  import flash.geom.Rectangle;

  import mx.containers.Panel;
  import mx.controls.Button;

  /**
   *
   */
  [Event(name="maximized")]
  /**
   *
   */
  [Event(name="minimized")]
  /**
   *
   */
  [Event(name="closePanel")]
  /**
   *
   */
  [Style(name="closeButtonStyle", type = "String")]
  /**
   *
   */
  [Style(name="maximizeButtonStyle", type = "String")]
  /**
   *
   */
  [Style(name="minimizeButtonStyle", type = "String")]


  /**
   *
   * @author Mateusz
   *
   */
  public class ResizeablePanel extends Panel {

    /**
     *
     */
    public static const MAXIMIZED : String = "maximized";

    /**
     *
     */
    public static const MINIMIZED : String = "minimized";


    /**
     *
     */
    public var _doubleClickResize : Boolean = true;

    /**
     *
     */
    private const DEFAULT_HEADER_BUTTON_SIZE : int = 20;

    /**
     *
     */
    private var _currentSizeState : String;

    /**
     *
     */
    private var closeButton : Button;

    /**
     *
     */
    private var maximizeButton : Button;

    /**
     *
     */
    private var minimizeButton : Button;

    /**
     *
     */
    private var oldProperties : Rectangle;


    /**
     *
     *
     */
    public function ResizeablePanel() {
      super();

      closeButton = new Button();
      maximizeButton = new Button();
      minimizeButton = new Button();

      _currentSizeState = MINIMIZED;
    }


    /**
     *
     * @param value
     *
     */
    public function set doubleClickResize(value : Boolean) : void {
      _doubleClickResize = value;
      titleBar.doubleClickEnabled = value;
    }


    /**
     *
     * @return
     *
     */
    public function get doubleClickResize() : Boolean {
      return _doubleClickResize;
    }


    /**
     *
     * @param value
     *
     */
    public function set currentSizeState(value : String) : void {
      if (value != _currentSizeState && (value == MAXIMIZED || value == MINIMIZED)) {
        _currentSizeState = value;

        if (_currentSizeState == MAXIMIZED) {
          maximize();
        } else if (_currentSizeState == MINIMIZED) {
          minimize();
        }
      }
    }


    /**
     *
     *
     */
    protected function maximize() : void {

      maximizeButton.visible = false;
      minimizeButton.visible = true;

      oldProperties = new Rectangle(x, y, width, height);

      x = NaN;
      y = NaN;

      percentHeight = 100;
      percentWidth = 100;

      dispatchEvent(new Event("maximize"));
    }


    /**
     *
     *
     */
    protected function minimize() : void {
      minimizeButton.visible = false;
      maximizeButton.visible = true;

      if (oldProperties) {
        x = oldProperties.x;
        y = oldProperties.y;
        width = oldProperties.width;
        height = oldProperties.height;
      }
      dispatchEvent(new Event("minimize"));
    }


    /**
     *
     * @return
     *
     */
    public function get currentSizeState() : String {
      return _currentSizeState;
    }


    /**
     *
     *
     */
    override protected function createChildren() : void {
      super.createChildren();

      closeButton.label = "close";
      closeButton.setActualSize(DEFAULT_HEADER_BUTTON_SIZE, DEFAULT_HEADER_BUTTON_SIZE);

      maximizeButton.label = "maximize";
      maximizeButton.setActualSize(DEFAULT_HEADER_BUTTON_SIZE, DEFAULT_HEADER_BUTTON_SIZE);

      minimizeButton.label = "minimize";
      minimizeButton.visible = false;
      minimizeButton.setActualSize(DEFAULT_HEADER_BUTTON_SIZE, DEFAULT_HEADER_BUTTON_SIZE);

      rawChildren.addChild(closeButton);
      rawChildren.addChild(maximizeButton);
      rawChildren.addChild(minimizeButton);

      titleBar.doubleClickEnabled = true;

      registerListeners();
    }


    /**
     *
     *
     */
    private function registerListeners() : void {
      closeButton.addEventListener(MouseEvent.CLICK, onCloseClick);
      maximizeButton.addEventListener(MouseEvent.CLICK, onMaximizeClick);
      minimizeButton.addEventListener(MouseEvent.CLICK, onMinimizeClick);
      titleBar.addEventListener(MouseEvent.DOUBLE_CLICK, onTitleBarDoubleClick);
    }


    /**
     *
     * @param event
     *
     */
    private function onCloseClick(event : MouseEvent) : void {
      dispatchEvent(new Event("closePanel"));
    }


    /**
     *
     * @param event
     *
     */
    private function onMaximizeClick(event : MouseEvent) : void {
      currentSizeState = MAXIMIZED;
    }


    /**
     *
     * @param event
     *
     */
    private function onMinimizeClick(event : MouseEvent) : void {
      currentSizeState = MINIMIZED;
    }


    /**
     *
     * @param event
     *
     */
    private function onTitleBarDoubleClick(event : MouseEvent) : void {
      if (doubleClickResize) {
        if (currentSizeState == MAXIMIZED) {
          currentSizeState = MINIMIZED;
        } else if (currentSizeState == MINIMIZED) {
          currentSizeState = MAXIMIZED;
        }
      }
    }


    /**
     *
     * @param unscaledWidth
     * @param unscaledHeight
     *
     */
    override protected function layoutChrome(unscaledWidth : Number, unscaledHeight : Number) : void {
      super.layoutChrome(unscaledWidth, unscaledHeight);

      closeButton.y = int((borderMetrics.top - closeButton.height) / 2);
      closeButton.x = unscaledWidth - borderMetrics.right - closeButton.width;
      maximizeButton.y = minimizeButton.y = closeButton.y;
      maximizeButton.x = minimizeButton.x = closeButton.x - maximizeButton.width - int((borderMetrics.top - closeButton.height) / 2);
    }


    /**
     *
     * @param unscaledWidth
     * @param unscaledHeight
     *
     */
    override protected function updateDisplayList(unscaledWidth : Number, unscaledHeight : Number) : void {
      super.updateDisplayList(unscaledWidth, unscaledHeight);
    }


    /**
     *
     * @param styleProp
     *
     */
    override public function styleChanged(styleProp : String) : void {
      if (getStyle("closeButtonStyle")) {
        closeButton.styleName = getStyle("closeButtonStyle");
      }

      if (getStyle("maximizeButtonStyle")) {
        maximizeButton.styleName = getStyle("maximizeButtonStyle");
      }

      if (getStyle("minimizedButtonStyle")) {
        minimizeButton.styleName = getStyle("minimizedButtonStyle");
      }
    }
  }
}