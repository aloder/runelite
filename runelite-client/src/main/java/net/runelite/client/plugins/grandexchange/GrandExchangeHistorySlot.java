/*
 * Copyright (c) 2018, aloder <https://github.com/aloder>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.grandexchange;

import net.runelite.api.GrandExchangeOffer;
import net.runelite.api.GrandExchangeOfferState;
import net.runelite.api.ItemComposition;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.StackFormatter;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GrandExchangeHistorySlot extends JPanel
{
	private final Color SOLD_COLOR = new Color(202, 30, 30);
	private final Color BOUGHT_COLOR = new Color(0, 176, 59);

	private final JPanel container = new JPanel();
	private final JLabel itemIcon = new JLabel();
	private final JLabel itemName = new JLabel();
	private final JLabel itemPrice = new JLabel();

	private final JLabel offerInfo = new JLabel();
	private final JLabel offerSpent = new JLabel();

	GrandExchangeHistorySlot(ItemComposition offerItem, BufferedImage itemImage, GrandExchangeOffer newOffer)
	{
		setLayout(new BorderLayout());
		setBackground(ColorScheme.DARK_GRAY_COLOR);
		setBorder(new EmptyBorder(4, 0, 0, 0));

		container.setLayout(new GridLayout(1, 1));
		container.setBorder(new EmptyBorder(0, 0, 4, 0));
		container.setBackground(ColorScheme.DARKER_GRAY_COLOR);

		SimpleDateFormat format = new SimpleDateFormat("hh:mm a");

		container.setToolTipText(format.format(new Date()));

		itemIcon.setVerticalAlignment(JLabel.CENTER);
		itemIcon.setHorizontalAlignment(JLabel.CENTER);
		itemIcon.setPreferredSize(new Dimension(45, 45));

		itemName.setForeground(Color.WHITE);
		itemName.setVerticalAlignment(JLabel.BOTTOM);
		itemName.setFont(FontManager.getRunescapeSmallFont());

		offerInfo.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
		offerInfo.setVerticalAlignment(JLabel.TOP);
		offerInfo.setFont(FontManager.getRunescapeSmallFont());

		offerSpent.setForeground(Color.WHITE);
		offerSpent.setVerticalAlignment(JLabel.TOP);
		offerSpent.setFont(FontManager.getRunescapeSmallFont());

		itemPrice.setForeground(Color.WHITE);
		itemPrice.setVerticalAlignment(JLabel.BOTTOM);
		itemPrice.setFont(FontManager.getRunescapeSmallFont());

		boolean buying = newOffer.getState() == GrandExchangeOfferState.BOUGHT
				|| newOffer.getState() == GrandExchangeOfferState.CANCELLED_BUY;

		String offerState = htmlLabel((buying ? "Bought " : "Sold "),
				StackFormatter.quantityToRSDecimalStack(newOffer.getQuantitySold()) + " / "
				+ StackFormatter.quantityToRSDecimalStack(newOffer.getTotalQuantity()));

		String action = buying ? "Spent: " : "Received: ";

		container.setBackground(buying ? BOUGHT_COLOR : SOLD_COLOR);


		itemName.setText(offerItem.getName());
		itemIcon.setIcon(new ImageIcon(itemImage));
		offerInfo.setText(offerState);

		offerSpent.setText(htmlLabel(action,
				StackFormatter.formatNumber(newOffer.getSpent())));

		itemPrice.setText(htmlLabel("Price each: ",
				StackFormatter.formatNumber(newOffer.getSpent() / newOffer.getQuantitySold())));

		JPanel historyCard = new JPanel();
		historyCard.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		historyCard.setLayout(new BorderLayout());

		JPanel historyDetails = new JPanel();
		historyDetails.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		historyDetails.setLayout(new GridLayout(4, 1, 0, 2));

		historyDetails.add(itemName);
		historyDetails.add(offerInfo);
		historyDetails.add(offerSpent);
		historyDetails.add(itemPrice);

		historyCard.add(itemIcon, BorderLayout.WEST);
		historyCard.add(historyDetails, BorderLayout.CENTER);

		container.add(historyCard);

		add(container, BorderLayout.CENTER);
		revalidate();
		repaint();
	}
	private String htmlLabel(String key, String value)
	{
		return "<html><body style = 'color:" + ColorUtil.toHexColor(ColorScheme.LIGHT_GRAY_COLOR) + "'>" + key + "<span style = 'color:white'>" + value + "</span></body></html>";
	}
}
