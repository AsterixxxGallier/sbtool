package com.example.demo

import javafx.beans.property.SimpleStringProperty
import javafx.scene.layout.BorderPane
import tornadofx.*

class Person(name: String? = null, title: String? = null) {
	val nameProperty = SimpleStringProperty(this, "name", name)
	var name: String by nameProperty

	val titleProperty = SimpleStringProperty(this, "title", title)
	var title: String by titleProperty
}

class MainView : View("Person List + Editor") {
	override val root = BorderPane()
	val list: PersonList by inject()
	val editor: PersonEditor by inject()

	init {
		root.center = list.root
		root.right = editor.root
	}
}

class PersonList : View("Person List") {
	val persons = listOf(Person("John", "Manager"), Person("Jay", "Worker bee")).observable()
	val model: PersonModel by inject()

	override val root = tableview(persons) {
		title = "Person"
		column("Name", Person::nameProperty)
		column("Title", Person::titleProperty)
		bindSelected(model)
	}
}

class PersonEditor : View("Person Editor") {
	val model: PersonModel by inject()

	override val root = form {
		fieldset("Edit person") {
			field("Name") {
				textfield(model.name)
			}
			field("Title") {
				textfield(model.title)
			}
			button("Save") {
				enableWhen(model.dirty)
				action {
					save()
				}
			}
			button("Reset").action {
				model.rollback()
			}
		}
	}

	private fun save() {
		model.commit()
		println("Saving ${model.item.name} / ${model.item.title}")
	}
}

class PersonModel(person: Person) : ItemViewModel<Person>(person) {
	val name = bind(Person::nameProperty)
	val title = bind(Person::titleProperty)
}