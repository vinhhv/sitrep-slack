package vinhhv.io.modals

import java.util

import com.slack.api.model.view.View
import com.slack.api.model.block.Blocks._
import com.slack.api.model.block.composition.BlockCompositions._
import com.slack.api.model.block.composition.{ MarkdownTextObject, OptionObject, PlainTextObject }
import com.slack.api.model.block.element.BlockElements._
import com.slack.api.model.block.element.{ DatePickerElement, PlainTextInputElement }
import com.slack.api.model.view.Views._

import scala.jdk.CollectionConverters._

object SitrepModal {
  val Sitrep = "sitrep"

  val CloseText  = "close_text"
  val PlainText  = "plain_text"
  val SubmitText = "schedule"

  val SitrepTitleId                       = "sitrep_title"
  val SitrepTitleText: MarkdownTextObject = markdownText("What's your *sitrep*? Schedule a status update!")

  val SitrepInputId = "status_input"
  val SitrepInput: PlainTextInputElement = plainTextInput(
      _.placeholder(plainText("ex. \":taco: Eating tacos for lunch!\"", true))
  )
  val SitrepInputText: PlainTextObject = plainText(
      """Status (prepend emoji to add a status icon)
        |
        |ex. \":taco: Eating tacos for lunch!\"
        |""".stripMargin
    , true
  )

  val SitrepDatepickerText: MarkdownTextObject       = markdownText("Pick a *date* and *time* to schedule your status:")
  val SitrepDatepickerPlaceholder: DatePickerElement = datePicker(_.placeholder(plainText("Select a date", true)))

  val SitrepHourSelectText: PlainTextObject   = plainText("Select the hour", true)
  val SitrepMinuteSelectText: PlainTextObject = plainText("Select the minute", true)

  val hours = List(12, 1, 2, 3, 4, 5, 6, 7, 8, 9, 11)
  val optionsHourAM: Seq[OptionObject] =
    hours
      .zipWithIndex
      .map(hour => option(plainText(s"${hour._1.toString} AM", true), s"hour-${hour._2}"))
  val optionsHourPM: Seq[OptionObject] =
    hours
      .zipWithIndex
      .map(hour => option(plainText(s"${hour._1.toString} PM", true), s"hour-${hour._2 + 12}"))
  val optionsHours: util.List[OptionObject] = (optionsHourAM ++ optionsHourPM).asJava

  val optionsMinutes: util.List[OptionObject] =
    (0 until 60)
      .map(minute => option(plainText(minute.toString, true), s"minute-$minute"))
      .asJava

  def buildView(shortcutCallbackId: String): View =
    view(view =>
      view
        .callbackId(shortcutCallbackId)
        .`type`("modal")
        .title(viewTitle(_.`type`(PlainText).text(Sitrep).emoji(true)))
        .submit(viewSubmit(_.`type`(PlainText).text(SubmitText).emoji(true)))
        .close(viewClose(_.`type`(PlainText).text(CloseText).emoji(true)))
        .blocks(
            asBlocks(
              section(section =>
              section
                .blockId(SitrepTitleId)
                .text(SitrepTitleText)
            )
            , input(input =>
              input
                .blockId(SitrepInputId)
                .element(SitrepInput)
                .label(SitrepInputText)
            )
            , divider
            , section(_.text(SitrepDatepickerText).accessory(SitrepDatepickerPlaceholder))
            , actions(
                asElements(
                  staticSelect(_.placeholder(SitrepHourSelectText).options(optionsHours))
                , staticSelect(_.placeholder(SitrepMinuteSelectText).options(optionsMinutes))
              )
            )
          )
        )
    )
}
