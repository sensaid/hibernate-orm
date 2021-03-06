/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2015, Red Hat Inc. or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Inc.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.hibernate.boot.model.source.internal.hbm;

import java.util.Collections;
import java.util.List;

import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmColumnType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmIndexType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmListIndexType;
import org.hibernate.boot.model.source.spi.PluralAttributeIndexNature;
import org.hibernate.boot.model.source.spi.PluralAttributeSequentialIndexSource;
import org.hibernate.boot.model.source.spi.RelationalValueSource;
import org.hibernate.boot.model.source.spi.SizeSource;
import org.hibernate.internal.util.StringHelper;

/**
 *
 */
public class PluralAttributeSequentialIndexSourceImpl
		extends AbstractHbmSourceNode
		implements PluralAttributeSequentialIndexSource {
	private final int base;
	private final String xmlNodeName;
	private final HibernateTypeSourceImpl typeSource;
	private final List<RelationalValueSource> valueSources;

	public PluralAttributeSequentialIndexSourceImpl(
			MappingDocument sourceMappingDocument,
			final JaxbHbmListIndexType jaxbListIndex) {
		super( sourceMappingDocument );
		this.base = Integer.parseInt( jaxbListIndex.getBase() );
		this.xmlNodeName = null;
		this.typeSource = new HibernateTypeSourceImpl( "integer" );
		this.valueSources = RelationalValueSourceHelper.buildValueSources(
				sourceMappingDocument,
				null,
				new RelationalValueSourceHelper.AbstractColumnsAndFormulasSource() {
					final List<JaxbHbmColumnType> columnElements = jaxbListIndex.getColumn() == null
							? Collections.<JaxbHbmColumnType>emptyList()
							: Collections.singletonList( jaxbListIndex.getColumn() );

					@Override
					public XmlElementMetadata getSourceType() {
						return XmlElementMetadata.LIST_INDEX;
					}

					@Override
					public String getSourceName() {
						return null;
					}

					@Override
					public String getColumnAttribute() {
						return jaxbListIndex.getColumnAttribute();
					}

					@Override
					public List getColumnOrFormulaElements() {
						return columnElements;
					}

				}
		);
	}

	public PluralAttributeSequentialIndexSourceImpl(
			MappingDocument sourceMappingDocument,
			final JaxbHbmIndexType jaxbIndex) {
		super( sourceMappingDocument );
		this.base = 0;
		this.xmlNodeName = null;
		if ( StringHelper.isEmpty( jaxbIndex.getType() ) ) {
			this.typeSource = new HibernateTypeSourceImpl( "integer" );
		}
		else {
			this.typeSource = new HibernateTypeSourceImpl( jaxbIndex.getType() );
		}
		this.valueSources = RelationalValueSourceHelper.buildValueSources(
				sourceMappingDocument,
				null,
				new RelationalValueSourceHelper.AbstractColumnsAndFormulasSource() {
					@Override
					public XmlElementMetadata getSourceType() {
						return XmlElementMetadata.INDEX;
					}

					@Override
					public String getSourceName() {
						return null;
					}

					@Override
					public String getColumnAttribute() {
						return jaxbIndex.getColumnAttribute();
					}

					@Override
					public SizeSource getSizeSource() {
						return Helper.interpretSizeSource(
								jaxbIndex.getLength(),
								(Integer) null,
								null
						);
					}

					@Override
					public List getColumnOrFormulaElements() {
						return jaxbIndex.getColumn();
					}
				}
		);
	}

	@Override
	public boolean areValuesIncludedInInsertByDefault() {
		return true;
	}

	@Override
	public boolean areValuesIncludedInUpdateByDefault() {
		return false;
	}

	@Override
	public boolean areValuesNullableByDefault() {
		return false;
	}

	@Override
	public int getBase() {
		return base;
	}

	@Override
	public PluralAttributeIndexNature getNature() {
		return PluralAttributeIndexNature.SEQUENTIAL;
	}

	@Override
	public HibernateTypeSourceImpl getTypeInformation() {
		return typeSource;
	}

	@Override
	public String getXmlNodeName() {
		return xmlNodeName;
	}

	@Override
	public List<RelationalValueSource> getRelationalValueSources() {
		return valueSources;
	}
}
