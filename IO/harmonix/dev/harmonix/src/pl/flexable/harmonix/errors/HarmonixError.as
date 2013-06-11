package pl.flexable.harmonix.errors {


  /**
   *
   * @author Mateusz
   *
   */
  public class HarmonixError extends Error {

    /**
     *
     */
    public static const ABSTRACT_METHOD_ERROR : String = "Abstract method error!";


    /**
     *
     */
    public static const SINGLETON_CLASS_ERROR : String = "Singleton class error!";


    /**
     *
     * @param message
     * @param id
     *
     */
    public function HarmonixError(message : String = "", id : int = 0) {
      super(message, id);
    }

  }
}