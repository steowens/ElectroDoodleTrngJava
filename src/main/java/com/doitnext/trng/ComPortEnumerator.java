package com.doitnext.trng;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fazecast.jSerialComm.SerialPort;

public class ComPortEnumerator {
	public static class ComPortInfo {
		private String name;
		private SerialPort spi;

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @param name the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}

		
		
		/**
		 * @return the spi
		 */
		public SerialPort getSpi() {
			return spi;
		}

		/**
		 * @param sp the spi to set
		 */
		public void setSpi(SerialPort spi) {
			this.spi = spi;
		}

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
		}

		@Override
		public int hashCode() {
			return new HashCodeBuilder().append(name).toHashCode();
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if(obj instanceof String)
				return new EqualsBuilder().append(this.name, (String)obj).isEquals();
			if(obj instanceof ComPortInfo)
				return new EqualsBuilder().append(this.name, ((ComPortInfo)obj).name).isEquals();
			if(obj instanceof SerialPort)
				return new EqualsBuilder().append(this.name, ((SerialPort)obj).getDescriptivePortName()).isEquals();
			return false;
		}
		
		
	}
	
	
	public static List<ComPortInfo> list(){
		List<ComPortInfo> result = new ArrayList<ComPortInfo>();
		SerialPort[] portsList = SerialPort.getCommPorts();
		for(int x= 0; x < portsList.length; x++) {
			SerialPort spi = portsList[x];
			ComPortInfo cpi = new ComPortInfo();
			cpi.setName(spi.getDescriptivePortName());
			cpi.setSpi(spi);
			result.add(cpi);
		}
		return result;
	}
}
