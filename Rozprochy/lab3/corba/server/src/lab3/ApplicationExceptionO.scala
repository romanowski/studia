package lab3

import Laboratory.ApplicationException

/**
 * Created by IntelliJ IDEA.
 * User: jar
 * Date: 22.04.12
 * Time: 18:07
 * To change this template use File | Settings | File Templates.
 */

object ApplicationExceptionO {

  def apply(message: String, ex: Throwable = null) = if (ex == null)
    new ApplicationException(message)
  else
    new ApplicationException(ex.getLocalizedMessage, message)

}
