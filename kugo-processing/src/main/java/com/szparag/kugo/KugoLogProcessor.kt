package com.szparag.kugo


import java.util.function.Consumer
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Completion
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.util.ElementFilter
import javax.tools.Diagnostic


class KugoLogProcessor : AbstractProcessor(), LogProcessor {

  override fun process(set: Set<TypeElement>, env: RoundEnvironment): Boolean {
    ElementFilter
        .typesIn(env.getElementsAnnotatedWith(KugoLog::class.java))
        .forEach(Consumer<TypeElement?> {
          when (it?.kind) {
            ElementKind.CLASS -> this::processClass
            ElementKind.CONSTRUCTOR -> this::processMethod
            ElementKind.METHOD -> this::processMethod
            else -> return@Consumer
          }
        })

    return false

  }

  override fun processClass(type: TypeElement) {
    processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "CLASS")

  }

  override fun processMethod(type: TypeElement) {
    processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "METHOD")

  }

  override fun writeToSource() {

  }

  override fun getSupportedOptions(): Set<String> {
    return super.getSupportedOptions()
  }

  override fun getSupportedAnnotationTypes(): Set<String> {
    return super.getSupportedAnnotationTypes()
  }

  override fun getSupportedSourceVersion(): SourceVersion {
    return super.getSupportedSourceVersion()
  }

  @Synchronized override fun init(processingEnvironment: ProcessingEnvironment) {
    super.init(processingEnvironment)
  }

  override fun getCompletions(element: Element?,
      annotationMirror: AnnotationMirror?, executableElement: ExecutableElement?,
      s: String?): Iterable<Completion> {
    return super.getCompletions(element, annotationMirror, executableElement, s)
  }

  @Synchronized override fun isInitialized(): Boolean {
    return super.isInitialized()
  }
}
