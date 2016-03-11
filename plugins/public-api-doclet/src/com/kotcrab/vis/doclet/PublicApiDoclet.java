package com.kotcrab.vis.doclet;

import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.RootDoc;
import com.sun.tools.doclets.standard.Standard;
import com.sun.tools.javac.code.Attribute.Compound;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symbol.PackageSymbol;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javadoc.Main;
import com.sun.tools.javadoc.PackageDocImpl;
import com.sun.tools.javadoc.RootDocImpl;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Generates Javadoc for classes annotated by VisEditor PublicApi annotation.
 * @author Kotcrab
 */
public class PublicApiDoclet {
	public static void main (String[] args) {
		String name = PublicApiDoclet.class.getName();
		Main.execute(name, name, args);
	}

	public static boolean validOptions (String[][] options, DocErrorReporter reporter)
			throws java.io.IOException {
		return Standard.validOptions(options, reporter);
	}

	public static int optionLength (String option) {
		return Standard.optionLength(option);
	}

	public static boolean start (RootDoc root) throws IOException, ReflectiveOperationException {
		return Standard.start(removeNotPublicApi(root));
	}

	//here be dragons
	private static RootDoc removeNotPublicApi (RootDoc rd) throws ReflectiveOperationException {
		if (rd == null) return null;

		RootDocImpl rootDoc = (RootDocImpl) rd;

		//obtain list (containing packages for javadoc) via reflection
		Field pckField = rootDoc.getClass().getDeclaredField("cmdLinePackages");
		pckField.setAccessible(true);
		List<PackageDocImpl> pckList = (List) pckField.get(rootDoc);

		//new filtered packages list
		ListBuffer<PackageDocImpl> pckNewList = new ListBuffer<>();

		for (PackageDocImpl pckImpl : pckList) {
			//obtain symbols field containing classes
			Field symField = pckImpl.getClass().getDeclaredField("sym");
			symField.setAccessible(true);
			PackageSymbol sym = (PackageSymbol) symField.get(pckImpl);

			removeNotPublicApiSymbols(sym);

			//if all classes were removed from package remove it
			//also there is no length lol
			if (sym.members_field.toString().equals("Scope[]") == false) {
				pckNewList.add(pckImpl);
			}
		}

		//finally swap new list, RootDoc won't notice
		pckField.set(rootDoc, pckNewList.toList());

		return rd;

	}

	private static void removeNotPublicApiSymbols (PackageSymbol sym) {
		//symbols that will be removed
		ArrayList<Symbol> symbolsToRemove = new ArrayList<>();

		for (Symbol s : sym.members_field.getElements()) {
			boolean skipSymbol = true;
			//search for PublicApi annotation using AnnotationMirrors (whatever they are)
			//don't even try to use s.getAnnotation(Class<A>)
			for (Compound c : s.getAnnotationMirrors()) {
				if (c.toString().equals("@com.kotcrab.vis.editor.util.PublicApi")) {
					skipSymbol = false;
					break;
				}
			}
			if (skipSymbol) symbolsToRemove.add(s);
		}

		for (Symbol s : symbolsToRemove) {
			sym.members_field.remove(s);
		}
	}
}
