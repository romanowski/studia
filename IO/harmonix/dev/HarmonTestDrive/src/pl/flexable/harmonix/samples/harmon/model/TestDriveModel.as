package pl.flexable.harmonix.samples.harmon.model {
  import mx.collections.ArrayCollection;



  /**
   *
   * @author Mateusz
   *
   */
  [Bindable]
  public class TestDriveModel {

    /**
     *
     */
    public var curses : ArrayCollection = new ArrayCollection();

    /**
     *
     */
    public var days : ArrayCollection = new ArrayCollection();


    private static var _instance : TestDriveModel;


    /**
     *
     *
     */
    public function TestDriveModel() {
    }


    /**
     *
     * @return
     *
     */
    public static function getInstance() : TestDriveModel {
      if (!_instance) {
        _instance = new TestDriveModel();
      }

      return _instance;
    }
  }
}