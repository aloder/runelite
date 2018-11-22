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

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import net.runelite.api.GrandExchangeOffer;
import net.runelite.api.GrandExchangeOfferState;
import net.runelite.api.ItemComposition;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.components.PluginErrorPanel;
import javax.swing.border.EmptyBorder;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class GrandExchangeHistoryPanel extends JPanel
{
	private static final String ERROR_PANEL = "ERROR_PANEL";
	private static final String HISTORY_PANEL = "HISTORY_PANEL";
	private static final int MAX_HISTORY = 40;

	private final GridBagConstraints constraints = new GridBagConstraints();
	private final CardLayout cardLayout = new CardLayout();

	/*  The offers container, this will hold all the individual ge offers panels */
	private final JPanel historyPanel = new JPanel();

	/*  The center panel, this holds either the error panel or the offers container */
	private final JPanel container = new JPanel(cardLayout);

	private final ArrayList<GrandExchangeHistorySlot> historySlotPanels = new ArrayList();
	GrandExchangeHistoryPanel()
	{
		setLayout(new BorderLayout());
		setBackground(ColorScheme.DARK_GRAY_COLOR);

		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 1;
		constraints.gridx = 0;
		constraints.gridy = 0;

		/* This panel wraps the offers panel and limits its height */
		JPanel historyWrapper = new JPanel(new BorderLayout());
		historyWrapper.setBackground(ColorScheme.DARK_GRAY_COLOR);
		historyWrapper.add(historyPanel, BorderLayout.NORTH);

		/* This wraps the list in a scroll pane - so that we can show a good amount of history */
		JScrollPane scrollWrapper = new JScrollPane(historyWrapper);
		scrollWrapper.setBackground(ColorScheme.DARK_GRAY_COLOR);
		scrollWrapper.getVerticalScrollBar().setPreferredSize(new Dimension(12, 0));
		scrollWrapper.getVerticalScrollBar().setBorder(new EmptyBorder(0, 5, 0, 0));
		scrollWrapper.setVisible(false);

		historyPanel.setLayout(new GridBagLayout());
		historyPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		historyPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);

		/* This panel wraps the error panel and limits its height */
		JPanel errorWrapper = new JPanel(new BorderLayout());
		errorWrapper.setBackground(ColorScheme.DARK_GRAY_COLOR);

		/*  The error panel, this displays an error message */
		PluginErrorPanel errorPanel = new PluginErrorPanel();
		errorWrapper.add(errorPanel, BorderLayout.NORTH);

		errorPanel.setBorder(new EmptyBorder(50, 20, 20, 20));
		errorPanel.setContent("No History Found", "No grand exchange history was found on your account.");

		container.add(scrollWrapper, HISTORY_PANEL);
		container.add(errorWrapper, ERROR_PANEL);

		add(container, BorderLayout.CENTER);

		updateEmptyHistoryPanel();
	}
	void updateHistory(ItemComposition item, BufferedImage itemImage, GrandExchangeOffer newOffer)
	{
		/* If we never sold or bought anything we are not going to add it */
		if (newOffer.getQuantitySold() == 0)
		{
			return;
		}
		/* checks if the offer is finalized - if so add it to the list and update the UI */
		if (newOffer.getState() == GrandExchangeOfferState.SOLD
				|| newOffer.getState() == GrandExchangeOfferState.CANCELLED_SELL
				|| newOffer.getState() == GrandExchangeOfferState.BOUGHT
				|| newOffer.getState() == GrandExchangeOfferState.CANCELLED_BUY)
		{
			GrandExchangeHistorySlot newSlot = new GrandExchangeHistorySlot(item, itemImage, newOffer);
			historySlotPanels.add(0, newSlot);

			reduceHistory();

			historyPanel.removeAll();
			for (GrandExchangeHistorySlot curItem : historySlotPanels)
			{
				historyPanel.add(curItem, constraints);
				constraints.gridy++;
			}

			revalidate();
			repaint();

			updateEmptyHistoryPanel();
		}


	}
	/* recursively checks if the ArrayList surpasses the maximum  */
	void reduceHistory()
	{
		if (historySlotPanels.size() > MAX_HISTORY)
		{
			historySlotPanels.remove(MAX_HISTORY);
			reduceHistory();
		}
	}
	/**
	 * Reset the border for the first offer slot.
	 */

	private void updateEmptyHistoryPanel()
	{
		if (historySlotPanels.isEmpty())
		{
			historyPanel.removeAll();
			cardLayout.show(container, ERROR_PANEL);
		}
		else
		{
			cardLayout.show(container, HISTORY_PANEL);
		}

	}
}
