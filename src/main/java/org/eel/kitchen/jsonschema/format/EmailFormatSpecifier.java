/*
 * Copyright (c) 2012, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eel.kitchen.jsonschema.format;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.main.ValidationFeature;
import org.eel.kitchen.jsonschema.report.ValidationMessage;
import org.eel.kitchen.jsonschema.report.ValidationReport;
import org.eel.kitchen.jsonschema.util.NodeType;
import org.eel.kitchen.jsonschema.validator.ValidationContext;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

/**
 * Validator for the {@code email} format specification.
 *
 * <p>Note: even though, stricto sensu, email addresses with no domain part
 * ARE valid, for practical reasons, this implementation chooses to mark
 * these emails as invalid,</p>
 *
 */
public final class EmailFormatSpecifier
    extends FormatSpecifier
{
    private static final FormatSpecifier instance = new EmailFormatSpecifier();

    private EmailFormatSpecifier()
    {
        super(NodeType.STRING);
    }

    public static FormatSpecifier getInstance()
    {
        return instance;
    }

    @Override
    void checkValue(final String fmt, final ValidationContext ctx,
        final ValidationReport report, final JsonNode instance)
    {
        // Yup, that is kind of misnamed. But the problem is with the
        // InternetAddress constructor in the first place which "enforces" a
        // syntax which IS NOT strictly RFC compliant.
        final boolean strictRFC
            = ctx.hasFeature(ValidationFeature.RFC_STRICT_CONFORMANCE);

        try {
            // Which means we actually invert it.
            new InternetAddress(instance.textValue(), !strictRFC);
        } catch (AddressException ignored) {
            final ValidationMessage.Builder msg = newMsg(fmt)
                .setMessage("string is not a valid email address")
                .addInfo("value", instance);
            report.addMessage(msg.build());
        }
    }
}
