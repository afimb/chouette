/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mobi.chouette.service;

import java.io.InputStream;
import lombok.Data;
import mobi.chouette.exchange.parameters.AbstractParameter;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;

/**
 *
 * @author marc
 */
@Data
public class FileResourceProperties {

        private AbstractParameter actionParameters;
        private ValidationParameters validationParameters;
        private InputStream actionDataInputStream;

        public FileResourceProperties(AbstractParameter actionParameters, ValidationParameters validationParameters,
                InputStream actionDataInputStream) {
            
            actionParameters = actionParameters;
            validationParameters = validationParameters;
            actionDataInputStream = actionDataInputStream;
            
        }
}
