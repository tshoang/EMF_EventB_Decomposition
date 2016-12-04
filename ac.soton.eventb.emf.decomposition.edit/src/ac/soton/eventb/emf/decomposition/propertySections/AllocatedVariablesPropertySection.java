/*******************************************************************************
 * Copyright (c) 2006,2007,2008 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package ac.soton.eventb.emf.decomposition.propertySections;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.jface.viewers.IFilter;
import org.eventb.emf.core.AbstractExtension;
import org.eventb.emf.core.Attribute;
import org.eventb.emf.core.CorePackage;
import org.eventb.emf.core.machine.Machine;
import org.eventb.emf.core.machine.MachinePackage;
import org.eventb.emf.core.machine.Variable;

import ac.soton.eventb.decomposition.DecompositionPackage;
import ac.soton.eventb.decomposition.Region;



/**
 * Variables tab table section.
 *
 * @author cfs
 */

public class AllocatedVariablesPropertySection extends AbstractTablePropertySection implements IFilter {

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IFilter#select(java.lang.Object)
	 */
	public boolean select(final Object selected) {
			return selected instanceof Region;
	}

	@Override
	protected EReference getFeature() {
		return DecompositionPackage.Literals.REGION__ALLOCATED_VARIABLES;
	}

	@Override
	protected EStructuralFeature getFeatureForCol(final int col) {
		switch (col) {
		case 0 : return CorePackage.Literals.EVENT_BNAMED__NAME;
		case 1 : return CorePackage.Literals.EVENT_BELEMENT__ATTRIBUTES;
		default : return null;
		}
	}

	@Override
	protected int columnWidth(final int col){
		switch (col) {
		case 0 : return super.columnWidth(col);	//name
		case 1 : return 400;					//comment
		default : return -1;
		}
	}

	/**
	 * the generatedBy column is read only
	 */
	protected boolean isReadOnly(final int col){
		return true;
	}
	
	/**
	 * generated variables rows are read only - i.e. cannot be removed from Region
	 * - user should remove the extension instead 
	 */
	protected boolean isReadOnly(final Object object){
		return getGeneratedBy(object)!=null;
	}

	/**
	 * Label for generatedBy column is not the feature name since we need to find the right attribute 
	 * in the attributes map
	 * 
	 * @param col
	 * @return
	 */
	protected String getColumnLabelText(int col) {
		if (col==1){	
			return "Generated By";
		}else{
			return super.getColumnLabelText(col);
		}
	}

	/**
	 * Overridden to get the 'generatedBy' String from an attribute
	 * 
	 * 
	 *@Override
	 */
	protected String getValueForCol(final Object object, int col) {
		if (col==1){
			return getGeneratedBy(object);
		}else{
			return super.getValueForCol(object, col);
		}
	}
	
	

	/*
	 * if this object is a variable and has a 'generatedBy' attribute,
	 * 	finds the type and name of the extension that generated this variable, 
	 * otherwise returns null 
	 * 
	 * @param object
	 * @return
	 */
	private String getGeneratedBy(final Object object) {
		if (object instanceof Variable){
			String key = "org.eventb.emf.persistence.generator_ID";
			EMap<String, Attribute> atts = ((Variable)object).getAttributes(); //.get(key)
			if (atts.containsKey(key)){
				Attribute genId = atts.get(key);
				String id = (String)genId.getValue();
				if (id!= null && id.length()>0){
					Machine m = (Machine) ((Variable)object).getContaining(MachinePackage.Literals.MACHINE);
					EList<AbstractExtension> extensions = m.getExtensions();
					for (AbstractExtension ext : extensions){
						String extId = ext.getExtensionId();
						if (id.equals(extId)){
							Object name = ext.eGet(ext.eClass().getEStructuralFeature("name"));
							if (name instanceof String){
								return ext.eClass().getName()+": "+(String)name;
							}else{
								return id;
							}
						}
					}
				}
				return "<EXTENSION NOT FOUND!> :- "+id;
			};
		}
		return null;
	}
	
	
}