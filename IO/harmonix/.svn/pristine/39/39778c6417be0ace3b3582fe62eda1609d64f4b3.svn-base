package pl.flexable.harmonix.clipboard {
  import flash.display.DisplayObject;
  import flash.events.EventDispatcher;
  import flash.events.FocusEvent;
  import flash.events.KeyboardEvent;
  import flash.events.MouseEvent;

  import mx.core.UIComponent;

  import pl.flexable.harmonix.common.Debug;
  import pl.flexable.harmonix.container.DataContainer;
  import pl.flexable.harmonix.events.ClipboardClientEvent;
  import pl.flexable.harmonix.interfaces.IClipboardClient;


  public class ClipboardClient extends EventDispatcher {

    /**
     *
     */
    private var target : IClipboardClient;

    /**
     *
     */
    private var clipboardManager : ClipboardManager;


    /**
     *
     * @param target
     *
     */
    public function ClipboardClient(target : IClipboardClient) {
      this.target = target;

      clipboardManager = ClipboardManager.getInstance();

      registerListeners();
    }


    /**
     *
     *
     */
    public function copy() : void {
      Debug.info("ClipboardClient.copy, target: " + target);
      var copiedItems : Array = target.copy()
      if (copiedItems && copiedItems.length > 0) {
        clipboardManager.addToClipboard(copiedItems);

        target.dispatchEvent(new ClipboardClientEvent(ClipboardClientEvent.COPY, copiedItems));
      }
    }


    /**
     *
     *
     */
    public function cut() : void {
      Debug.info("ClipboardClient.cut, target: " + target);
      var cutItems : Array = target.copy();
      if (cutItems && cutItems.length > 0) {
        clipboardManager.addToClipboard(cutItems);

        for each (var item : *in cutItems) {
          if (item is DisplayObject) {
            UIComponent(target).removeChild(item);
          } else {
            DataContainer(target).removeDataChild(item);
          }
        }

        target.dispatchEvent(new ClipboardClientEvent(ClipboardClientEvent.CUT, cutItems));
      }
    }


    /**
     *
     *
     */
    public function paste() : void {
      Debug.info("ClipboardClient.paste, target: " + target);
      if (clipboardManager.clipboard) {
        var pasteItems : Array = clipboardManager.clipboard.source;
        if (pasteItems && pasteItems.length > 0) {
          target.paste(pasteItems);
          target.dispatchEvent(new ClipboardClientEvent(ClipboardClientEvent.PASTE, pasteItems));
        }
      }
    }


    /**
     *
     * @return
     *
     */
    public function getSelectedItems() : Array {
      return null;
    }


    /**
     *
     * @param event
     *
     */
    private function onTargetKeyDown(event : KeyboardEvent) : void {
      if (!target.clipboardEnabled) {
        return;
      }
      if (event.ctrlKey) {
        switch (event.keyCode) {
          // "ctrl + x" 
          case 88:
            cut();
            break;
          // "ctrl + c" 
          case 67:
            copy();
            break;
          // "ctrl + v" 
          case 86:
            paste();
            break;
        }
      }
    }


    /**
     *
     * @param event
     *
     */
    private function onTargetFocusIn(event : FocusEvent) : void {
    }


    /**
     *
     * @param event
     *
     */
    private function onTargetFocusOut(event : FocusEvent) : void {
    }


    /**
     *
     * @param event
     *
     */
    private function onTargetClick(event : MouseEvent) : void {
    }


    /**
     *
     *
     */
    private function registerListeners() : void {
      target.addEventListener(KeyboardEvent.KEY_DOWN, onTargetKeyDown);
      target.addEventListener(FocusEvent.FOCUS_IN, onTargetFocusIn);
      target.addEventListener(FocusEvent.FOCUS_OUT, onTargetFocusOut);
      target.addEventListener(MouseEvent.CLICK, onTargetClick);
    }


    /**
     *
     *
     */
    private function unregisterListeners() : void {
      target.removeEventListener(KeyboardEvent.KEY_DOWN, onTargetKeyDown);
      target.removeEventListener(FocusEvent.FOCUS_IN, onTargetFocusIn);
      target.removeEventListener(FocusEvent.FOCUS_OUT, onTargetFocusOut);
      target.removeEventListener(MouseEvent.CLICK, onTargetClick);
    }

  }
}